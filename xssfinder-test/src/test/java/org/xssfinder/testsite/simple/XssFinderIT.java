package org.xssfinder.testsite.simple;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xssfinder.XssFinder;
import org.xssfinder.remote.ExecutorServer;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.reporting.XssSighting;
import org.xssfinder.scanner.ExecutorServerFactory;

import java.io.File;

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
        assertThat(journal.getErrorContexts().size(), is(2)); // throwException is called on both runs (attack / observe)
        XssSighting sighting = journal.getXssSightings().iterator().next();
        assertThat(sighting.getSubmitMethodName(), is("unsafeSubmit"));
        assertThat(sighting.getInputIdentifier(), is("//form[@id=\"unsafeForm\"]/input[1]"));
        assertThat(journal.getPagesClassWithUntestedInputs().size(), is(1));
        assertThat(new File(OUTPUT_FILE).exists(), is(true));
    }
}
