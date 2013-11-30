package org.xssfinder.php;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.*;
import org.xssfinder.XssFinder;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.reporting.XssSighting;

import java.io.*;
import java.net.*;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeNoException;

public class XssFinderPhpIT
{
    private static final String OUTPUT_FILE = "xssfinder_int_test_report.html";

    private static Process seleniumProcess = null;

    private Process serverProcess = null;

    @BeforeClass
    public static void setUpClass() throws Exception {
        startSelenium();
        startTestWebApp();
    }

    @AfterClass
    public static void tearDownClass() {
        if (seleniumProcess != null) {
            seleniumProcess.destroy();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @After
    public void tearDown() {
        if (serverProcess != null) {
            serverProcess.destroy();
        }

        new File(OUTPUT_FILE).delete();
    }

    @Test
    public void runXssFinder() throws Exception {
        // given
        startPhpExecutor();

        // when
        XssFinder xssFinder = new XssFinder("localhost", 9090, 5000);
        XssJournal journal = xssFinder.findXssVulnerabilities("XssFinder\\TestSite\\Page", OUTPUT_FILE);

        // then
        assertThat(journal.getDescriptorById("1"), is(not(nullValue())));
        assertThat(journal.getXssSightings().size(), is(1));
        // There are two errors recorded for each error (once in the attack phase, once in the detect phase). There
        // are two errors each for:
        //  - the page method throwException()
        //  - the afterRoute handler trying to log out when login failed
        //TODO: afterRoute not implemented, so only 2 for now
        assertThat(journal.getErrorContexts().size(), is(2));
        XssSighting sighting = journal.getXssSightings().iterator().next();
        assertThat(sighting.getSubmitMethodName(), is("unsafeSubmit"));
        assertThat(sighting.getInputIdentifier(), is("//form[@id=\"unsafeForm\"]/input[1]"));
        assertThat(journal.getPagesClassWithUntestedInputs().size(), is(1));
        assertThat(new File(OUTPUT_FILE).exists(), is(true));
    }

    private static void startSelenium() throws Exception {
        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("java", "-jar", "vendor/selenium/selenium-server-standalone-2.35.0.jar");
            seleniumProcess = pb.start();

            // Read from standard out & error
            StreamGobbler errorGobbler = new StreamGobbler(seleniumProcess.getErrorStream(), "sel:err:");
            StreamGobbler outputGobbler = new StreamGobbler(seleniumProcess.getInputStream(), "sel:out:");
            errorGobbler.start();
            outputGobbler.start();
        } catch (Exception e) {
            // If we failed to run Selenium, skip the test
            assumeNoException("Failed to run Selenium due to an exception", e);
        }

        long now = new Date().getTime();
        boolean seleniumIsReady = false;
        while (!seleniumIsReady && (new Date().getTime() - now < 5000))
        {
            try {
                String urlString = "http://localhost:4444/wd/hub/status";
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                int status = connection.getResponseCode();
                seleniumIsReady = (status == 200);
            } catch (ConnectException e) {
                // Server is still not ready
            }
        }
        if (!seleniumIsReady) {
            throw new Exception("Selenium did not start in time");
        }
    }

    private static void startTestWebApp() throws Exception {
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
        webAppContext.setClassLoader(XssFinderPhpIT.class.getClassLoader());
        server.setHandler(webAppContext);

        server.start();
    }

    private void startPhpExecutor() throws InterruptedException {
        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("php", "test/XssFinder/TestSite/_runserver.php");
            serverProcess = pb.start();

            // Read from standard out & error
            StreamGobbler errorGobbler = new StreamGobbler(serverProcess.getErrorStream(), "php:err:");
            StreamGobbler outputGobbler = new StreamGobbler(serverProcess.getInputStream(), "php:out:");
            errorGobbler.start();
            outputGobbler.start();
        } catch (Exception e) {
            // If we failed to run PHP, skip the test
            assumeNoException("Failed to run PHP due to an exception", e);
        }

        // Wait for the PHP executor to start up
        //TODO: Find a better way to do this
        Thread.sleep(1000);
    }

    private static final class StreamGobbler extends Thread {
        InputStream is;
        String type;

        StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(type + ">" + line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
