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
import org.xssfinder.xss.XssAttackFactory;
import org.xssfinder.xss.XssGenerator;
import org.xssfinder.xss.XssJournal;

import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
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
        PageAttacker pageAttacker = new PageAttacker(new XssGenerator(new XssAttackFactory()), new XssDescriptorFactory());
        RouteRunner runner = new RouteRunner(pageAttacker, driverWrapper, new PageTraverser(), journal, routes);

        // Run!
        runner.run();

        assertThat(journal.getDescriptorById("1"), is(not(nullValue())));
    }
}
