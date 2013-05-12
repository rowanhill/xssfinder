package org.xssfinder.testsite.simple;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Before;
import org.junit.Test;
import org.xssfinder.routing.GraphsFactory;
import org.xssfinder.routing.Route;
import org.xssfinder.routing.RouteGenerator;
import org.xssfinder.runner.*;
import org.xssfinder.scanner.PageFinder;
import org.xssfinder.testsite.simple.page.HomePage;
import org.xssfinder.xss.*;

import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class XssFinderIT {

    @Before
    public void setUp() throws Exception {
        if (System.getProperty("jetty.port") != null) {
            return;
        }
        Server server = new Server(8085);
        server.setStopAtShutdown(true);

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setResourceBase("xssfinder-test/src/main/webapp");
        webAppContext.setDescriptor("WEB-INF/web.xml");
        webAppContext.setClassLoader(getClass().getClassLoader());
        server.setHandler(webAppContext);

        server.start();
    }

    @Test
    public void runXssFinder() {
        // Find all the page classes
        Set<Class<?>> pageClasses = new PageFinder("org.xssfinder.testsite.simple").findAllPages();

        // Generate routes from the page object network
        RouteGenerator routeGenerator = new RouteGenerator(new GraphsFactory());
        List<Route> routes = routeGenerator.generateRoutes(pageClasses);

        // Create a runner using HtmlUnitDriver
        DefaultHtmlUnitDriverWrapper driverWrapper = new DefaultHtmlUnitDriverWrapper();
        XssJournal journal = new XssJournal();
        RoutePageStrategyRunner strategyRunner = new RoutePageStrategyRunner(driverWrapper, driverWrapper.getPageInstantiator(), new PageTraverser());
        PageAttacker pageAttacker = new PageAttacker(new XssGenerator(new XssAttackFactory()), new XssDescriptorFactory());
        AttackPageStrategy attackStrategy = new AttackPageStrategy(pageAttacker, journal);
        DetectSuccessfulXssPageStrategy detectStrategy = new DetectSuccessfulXssPageStrategy(new XssDetector(), journal);
        RouteRunner runner = new RouteRunner(strategyRunner, attackStrategy, detectStrategy);

        // Run!
        runner.run(routes);

        assertThat(journal.getDescriptorById("1"), is(not(nullValue())));
        assertThat(journal.getSuccessfulXssDescriptors().size(), is(1));
        XssDescriptor descriptor = journal.getSuccessfulXssDescriptors().iterator().next();
        assertThat(descriptor.getPageClass() == HomePage.class, is(true));
        assertThat(descriptor.getInputIdentifier(), is("body/form[1]/input[1]"));
    }
}
