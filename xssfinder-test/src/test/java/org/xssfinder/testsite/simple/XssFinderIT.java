package org.xssfinder.testsite.simple;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.reporting.XssSighting;
import org.xssfinder.reporting.XssSightingFactory;
import org.xssfinder.routing.Route;
import org.xssfinder.routing.RouteGenerator;
import org.xssfinder.routing.RouteGeneratorFactory;
import org.xssfinder.runner.*;
import org.xssfinder.scanner.PageFinder;

import java.io.File;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class XssFinderIT {

    private static final String OUTPUT_FILE = "xssfinder_int_test_report.html";

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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @After
    public void tearDown() {
        new File(OUTPUT_FILE).delete();
    }

    @Test
    public void runXssFinder() throws Exception {
        // Find all the page classes
        Set<Class<?>> pageClasses = new PageFinder("org.xssfinder.testsite.simple").findAllPages();

        // Generate routes from the page object network
        RouteGeneratorFactory routeGeneratorFactory = new RouteGeneratorFactory();
        RouteGenerator routeGenerator = routeGeneratorFactory.createRouteGenerator();
        List<Route> routes = routeGenerator.generateRoutes(pageClasses);

        // Create a runner using HtmlUnitDriver
        RouteRunnerFactory runnerFactory = new RouteRunnerFactory();
        DefaultHtmlUnitDriverWrapper driverWrapper = new DefaultHtmlUnitDriverWrapper();
        XssJournal journal = new XssJournal(new XssSightingFactory());
        RouteRunner runner = runnerFactory.createRouteRunner(driverWrapper, OUTPUT_FILE);

        // Run!
        runner.run(routes, journal);

        assertThat(routes.size(), is(4));
        assertThat(journal.getDescriptorById("1"), is(not(nullValue())));
        assertThat(journal.getXssSightings().size(), is(1));
        assertThat(journal.getErrorContexts().size(), is(2)); // throwException is called on both runs (attack / observe)
        XssSighting sighting = journal.getXssSightings().iterator().next();
        assertThat(sighting.getSubmitMethodName(), is("unsafeSubmit"));
        assertThat(sighting.getInputIdentifier(), is("//form[@id=\"unsafeForm\"]/input[1]"));
        assertThat(journal.getPagesClassWithUntestedInputs().size(), is(1));
        assertThat(new File(OUTPUT_FILE).exists(), is(true));
    }
}
