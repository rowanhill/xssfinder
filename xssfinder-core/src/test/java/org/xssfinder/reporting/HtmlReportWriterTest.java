package org.xssfinder.reporting;

import com.google.common.collect.ImmutableSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.xssfinder.runner.PageContext;
import org.xssfinder.xss.XssDescriptor;

import java.io.File;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HtmlReportWriterTest {
    private static final String OUT_FILE = "report.html";

    @Mock
    private XssJournal mockJournal;

    @Mock
    private XssSightingFactory mockXssSightingFactory;

    private final HtmlReportWriter reportWriter = new HtmlReportWriter(OUT_FILE);
    private final XssJournal journal = new XssJournal(mockXssSightingFactory);

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
        XssSighting mockXssSighting = mock(XssSighting.class);
        when(mockXssSighting.getVulnerableClassName()).thenReturn("org.SomeClass");
        when(mockXssSighting.getSightingClassName()).thenReturn("org.SomeResultsClass");
        when(mockXssSighting.getSubmitMethodName()).thenReturn("submitForm");
        when(mockXssSighting.getInputIdentifier()).thenReturn("/some/xpath");
        Set<XssSighting> expectedSightings = ImmutableSet.of(mockXssSighting);
        when(mockJournal.getXssSightings()).thenReturn(expectedSightings);

        // when
        reportWriter.write(mockJournal);

        // then
        ReportPage reportPage = new ReportPage(createDriver());
        assertThat(reportPage.hasEntryForClassAndMethodAndInput(
                "org.SomeClass",
                "org.SomeResultsClass",
                "submitForm()",
                "/some/xpath"), is(true));
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

        public boolean hasEntryForClassAndMethodAndInput(
                String vulnerableClassName,
                String sightingClassName,
                String methodString,
                String identifier
        ) {
            return webDriver.findElements(By.xpath(
                    "//table[@id='vulnerabilities']//td[1][contains(text(),'" + vulnerableClassName +"')]" +
                            "/../td[2][contains(text(),'"+methodString+"')]" +
                            "/../td[3][contains(text(),'"+identifier+"')]" +
                            "/../td[4][contains(text(),'"+sightingClassName+"')]"
            )).size() == 1;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class SomePage {
        SomePage submitForm() { return null; }
    }
}
