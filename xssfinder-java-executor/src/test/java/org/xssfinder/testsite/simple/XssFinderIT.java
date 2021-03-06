package org.xssfinder.testsite.simple;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xssfinder.XssFinder;
import org.xssfinder.remote.ExecutorServer;
import org.xssfinder.remote.ExecutorServerFactory;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.reporting.XssSighting;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class XssFinderIT {

    private static final String OUTPUT_FILE = "xssfinder_int_test_report.html";

    @Before
    public void setUp() throws Exception {
        if (System.getProperty("jetty.port") != null) {
            // If we're running in Maven, Jetty is already running
            return;
        }

        // Start the Jetty server running, referencing the xssfinder-testwebapp project. Note: this is slightly
        // different to the maven tests, which builds the war first and deploys that. This shouldn't have any
        // impact on the tests.

        Server server = new Server(8085);
        server.setStopAtShutdown(true);

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setResourceBase("xssfinder-testwebapp/src/main/webapp");
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
        // given
        ExecutorServerFactory executorServerFactory = new ExecutorServerFactory();
        final ExecutorServer executorServer = executorServerFactory.createExecutorServer(9091);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                executorServer.serve();
            }
        });
        thread.setDaemon(true);
        thread.start();

        // when
        XssFinder xssFinder = new XssFinder("localhost", 9091, 5000);
        XssJournal journal = xssFinder.findXssVulnerabilities("org.xssfinder.testsite.simple", OUTPUT_FILE);

        // then
        assertThat(journal.getDescriptorById("1"), is(not(nullValue())));
        assertThat(journal.getXssSightings().size(), is(1));
        // There are two errors recorded for each error (once in the attack phase, once in the detect phase). There
        // are two errors each for:
        //  - the page method throwException()
        //  - the afterRoute handler trying to log out when login failed
        assertThat(journal.getErrorContexts().size(), is(4));
        XssSighting sighting = journal.getXssSightings().iterator().next();
        assertThat(sighting.getSubmitMethodName(), is("unsafeSubmit"));
        assertThat(sighting.getInputIdentifier(), is("//form[@id=\"unsafeForm\"]/input[1]"));
        assertThat(journal.getPagesClassWithUntestedInputs().size(), is(1));
        assertThat(new File(OUTPUT_FILE).exists(), is(true));
    }
}
