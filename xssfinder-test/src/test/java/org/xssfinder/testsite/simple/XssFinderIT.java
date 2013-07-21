package org.xssfinder.testsite.simple;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xssfinder.reflection.Instantiator;
import org.xssfinder.remote.*;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.reporting.XssSighting;
import org.xssfinder.routing.Route;
import org.xssfinder.routing.RouteGenerator;
import org.xssfinder.routing.RouteGeneratorFactory;
import org.xssfinder.runner.*;
import org.xssfinder.scanner.MethodDefinitionFactory;
import org.xssfinder.scanner.PageDefinitionFactory;
import org.xssfinder.scanner.PageFinder;
import org.xssfinder.scanner.ThriftToReflectionLookupFactory;
import org.xssfinder.xss.XssAttackFactory;
import org.xssfinder.xss.XssGenerator;

import java.io.File;
import java.util.HashMap;
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
        // Set up the remote executor
        ExecutorHandler executorHandler = new ExecutorHandler(
                new PageFinder(),
                new PageDefinitionFactory(
                        new MethodDefinitionFactory(),
                        new HashMap<Class<?>, PageDefinition>()
                ),
                new ThriftToReflectionLookupFactory(),
                new ExecutorContext(
                        new DefaultHtmlUnitDriverWrapper(),
                        new XssGenerator(new XssAttackFactory()),
                        new PageTraverser(
                                new CustomTraverserInstantiator(new Instantiator()),
                                new CustomSubmitterInstantiator(new Instantiator()),
                                new LabelledXssGeneratorFactory()
                        ),
                        new Instantiator(),
                        new LifecycleEventExecutor()
                )
        );
        final ExecutorServer executorServer = new ExecutorServer(9091, executorHandler);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                executorServer.serve();
            }
        });
        thread.setDaemon(true);
        thread.start();

        // TODO: Encapsulate creating the coordinator & getting the routes into a new class. Should be a one-liner.
        // Set up the coordinator
        RouteRunnerFactory runnerFactory = new RouteRunnerFactory();
        TTransport transport = new TSocket("localhost", 9091, 5000);
        Executor.Client client;
        try {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            client = new Executor.Client(protocol);
            ExecutorWrapper executorWrapper = new ExecutorWrapper(client);
            RouteRunner runner = runnerFactory.createRouteRunner(executorWrapper, OUTPUT_FILE);

            // Get the routes
            Set<PageDefinition> pageDefinitions = client.getPageDefinitions("org.xssfinder.testsite.simple");
            RouteGeneratorFactory routeGeneratorFactory = new RouteGeneratorFactory();
            RouteGenerator routeGenerator = routeGeneratorFactory.createRouteGenerator();
            List<Route> routes = routeGenerator.generateRoutes(pageDefinitions);

            // Run!
            XssJournal journal = runner.run(routes);

            assertThat(routes.size(), is(4));
            assertThat(journal.getDescriptorById("1"), is(not(nullValue())));
            assertThat(journal.getXssSightings().size(), is(1));
            assertThat(journal.getErrorContexts().size(), is(2)); // throwException is called on both runs (attack / observe)
            XssSighting sighting = journal.getXssSightings().iterator().next();
            assertThat(sighting.getSubmitMethodName(), is("unsafeSubmit"));
            assertThat(sighting.getInputIdentifier(), is("//form[@id=\"unsafeForm\"]/input[1]"));
            assertThat(journal.getPagesClassWithUntestedInputs().size(), is(1));
            assertThat(new File(OUTPUT_FILE).exists(), is(true));
        } finally {
            transport.close();
        }
    }
}
