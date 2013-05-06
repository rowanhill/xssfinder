package org.xssfinder.testsite.simple;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Before;
import org.junit.Test;
import org.xssfinder.routing.GraphsFactory;
import org.xssfinder.routing.Route;
import org.xssfinder.routing.RouteGenerator;
import org.xssfinder.runner.DefaultHtmlUnitDriverWrapper;
import org.xssfinder.runner.PageTraverser;
import org.xssfinder.runner.RouteRunner;
import org.xssfinder.scanner.PageFinder;
import org.xssfinder.xss.XssGenerator;

import java.util.List;
import java.util.Set;

public class XssFinderTest {

    @Before
    public void setUp() throws Exception {
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
        RouteRunner runner = new RouteRunner(driverWrapper, new PageTraverser(), new XssGenerator(), routes);

        // Run!
        runner.run();
    }
}
