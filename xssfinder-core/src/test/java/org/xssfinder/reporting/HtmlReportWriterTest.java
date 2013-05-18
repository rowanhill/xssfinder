package org.xssfinder.reporting;

import com.google.common.collect.ImmutableSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.xssfinder.xss.XssDescriptor;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class HtmlReportWriterTest {
    private static final String OUT_FILE = "report.html";

    private  HtmlReportWriter reportWriter = new HtmlReportWriter(OUT_FILE);
    private XssJournal journal = new XssJournal();

    @Before
    public void setUp() {
        File file = new File(OUT_FILE);
        boolean didDelete = file.delete();
        if (didDelete) {
            System.err.println("Warning: Had to delete " + OUT_FILE);
        }
    }

    @After
    public void tearDown() {
        File file = new File(OUT_FILE);
        boolean didDelete = file.delete();
        if (!didDelete) {
            System.err.println("Warning: Could not find expected file " + OUT_FILE);
        }
    }

    @Test
    public void createsFileAtLocationSpecified() throws Exception {
        // when
        reportWriter.write(journal);

        // then
        File file = new File(OUT_FILE);
        assertThat(file.exists(), is(true));
    }

    @Test
    public void reportFileDoesNotContainPageClassNamesWithoutVulnerabilities() throws Exception {
        // given
        journal.addXssDescriptor("1", new XssDescriptor(SomePage.class.getDeclaredMethod("submitForm"), "/some/xpath"));

        // when
        reportWriter.write(journal);

        // then
        ReportPage reportPage = new ReportPage(createDriver());
        assertThat(reportPage.hasEntryForClass(SomePage.class), is(false));
    }

    @Test
    public void reportFileContainsPageClassNamesWithXssVulnerabilities() throws Exception {
        // given
        journal.addXssDescriptor("1", new XssDescriptor(SomePage.class.getDeclaredMethod("submitForm"), "/some/xpath"));
        journal.markAsSuccessful(ImmutableSet.of("1"));

        // when
        reportWriter.write(journal);

        // then
        ReportPage reportPage = new ReportPage(createDriver());
        assertThat(reportPage.hasEntryForClassAndMethodAndInput(SomePage.class, "submitForm()", "/some/xpath"), is(true));
    }

    private HtmlUnitDriver createDriver() throws Exception {
        File file = new File(OUT_FILE);
        HtmlUnitDriver webDriver = new HtmlUnitDriver();
        webDriver.navigate().to(file.toURI().toURL());
        return webDriver;
    }

    private static class ReportPage {
        private final HtmlUnitDriver webDriver;

        private ReportPage(HtmlUnitDriver webDriver) {
            this.webDriver = webDriver;
        }

        public boolean hasEntryForClass(Class<?> pageClass) {
            return webDriver.findElements(By.xpath(
                    "//table[@id='vulnerabilities']//td[1][contains(text()," + pageClass.getCanonicalName() +")]"
            )).size() == 1;
        }

        public boolean hasEntryForClassAndMethodAndInput(Class<?> pageClass, String methodString, String identifier) {
            return webDriver.findElements(By.xpath(
                    "//table[@id='vulnerabilities']//td[1][contains(text(),'" + pageClass.getCanonicalName() +"')]" +
                            "/../td[2][contains(text(),'"+methodString+"')]" +
                            "/../td[3][contains(text(),'"+identifier+"')]"
            )).size() == 1;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class SomePage {
        SomePage submitForm() { return null; }
    }
}
