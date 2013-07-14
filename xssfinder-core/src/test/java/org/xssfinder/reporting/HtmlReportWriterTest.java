package org.xssfinder.reporting;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.xssfinder.remote.PageDefinition;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore("NoClassDefFoundError: org/apache/http/pool/ConnPoolControl")
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
        File file = new File(OUT_FILE).getAbsoluteFile();
        boolean didDelete = file.delete();
        if (didDelete) {
            System.err.println("Warning: Had to delete " + OUT_FILE);
        }
    }

    @After
    public void tearDown() {
        File file = new File(OUT_FILE).getAbsoluteFile();
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
        File file = new File(OUT_FILE).getAbsoluteFile();
        assertThat(file.exists(), is(true));
    }

    @Test
    public void reportFileDoesNotContainPageClassNamesWithoutVulnerabilities() throws Exception {
        // when
        reportWriter.write(journal);

        // then
        ReportPage reportPage = new ReportPage(createDriver());
        assertThat(reportPage.getVulnerabilityCount(), is(1));
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

    @Test
    public void reportFileContainsWarningsSectionWhichIsEmptyByDefault() throws Exception {
        // when
        reportWriter.write(mockJournal);

        // then
        ReportPage reportPage = new ReportPage(createDriver());
        assertThat(reportPage.getWarningsCount(), is(0));
    }

    @Test
    public void reportFileContainsWarningsSectionWithRowForEachPageWithUntestedInputs() throws Exception {
        // given
        PageDefinition mockPageDefinition1 = mock(PageDefinition.class);
        when(mockPageDefinition1.getIdentifier()).thenReturn("Mock Page 1");
        PageDefinition mockPageDefinition2 = mock(PageDefinition.class);
        when(mockPageDefinition1.getIdentifier()).thenReturn("Mock Page 2");
        Set<PageDefinition> pageClassesWithUntestedInputs = ImmutableSet.of(
                mockPageDefinition1,
                mockPageDefinition2
        );
        when(mockJournal.getPagesClassWithUntestedInputs()).thenReturn(pageClassesWithUntestedInputs);

        // when
        reportWriter.write(mockJournal);

        // then
        ReportPage reportPage = new ReportPage(createDriver());
        assertThat(reportPage.getWarningsCount(), is(2));
        assertThat(reportPage.hasWarningForClass("Mock Page 1"), is(true));
        assertThat(reportPage.hasWarningForClass("Mock Page 2"), is(true));
    }

    @Test
    public void reportFileContainsEmptyErrorsSectionIfNoExceptionsWereRaised() throws Exception {
        // when
        reportWriter.write(mockJournal);

        // then
        ReportPage reportPage = new ReportPage(createDriver());
        assertThat(reportPage.getErrorsCount(), is(0));
    }

    @Test
    public void reportFileContainsErrorRowForEachErrorContextInJournal() throws Exception {
        // given
        RouteRunErrorContext mockContext = mock(RouteRunErrorContext.class);
        List<RouteRunErrorContext> errorContexts = ImmutableList.of(mockContext);
        when(mockJournal.getErrorContexts()).thenReturn(errorContexts);

        // when
        reportWriter.write(mockJournal);

        // then
        ReportPage reportPage = new ReportPage(createDriver());
        assertThat(reportPage.getErrorsCount(), is(errorContexts.size()*2));
        assertTrue(reportPage.hasErrorForContext(mockContext));
    }

    @Test
    public void errorRowContainsBasicDetailsOnError() throws Exception {
        // given
        RouteRunErrorContext mockContext = mock(RouteRunErrorContext.class);
        when(mockContext.getPageIdentifier()).thenReturn("Page class");
        when(mockContext.getPageTraversalMethodString()).thenReturn("Traversal method");
        when(mockContext.getTraversalModeName()).thenReturn("Normal");
        List<RouteRunErrorContext> errorContexts = ImmutableList.of(mockContext);
        when(mockJournal.getErrorContexts()).thenReturn(errorContexts);

        // when
        reportWriter.write(mockJournal);

        // then
        ReportPage reportPage = new ReportPage(createDriver());
        ErrorReportRow row = reportPage.getErrorRowForContext(mockContext);
        assertThat(row.getPageClass(), is("Page class"));
        assertThat(row.getTraversalMethod(), is("Traversal method"));
        assertThat(row.getTraversalMode(), is("Normal"));
    }

    @Test
    public void errorRowExceptionDetailsAreHiddenButCanBeShown() throws Exception {
        RouteRunErrorContext mockContext = mock(RouteRunErrorContext.class);
        List<RouteRunErrorContext> errorContexts = ImmutableList.of(mockContext);
        when(mockJournal.getErrorContexts()).thenReturn(errorContexts);

        // when
        reportWriter.write(mockJournal);

        // then
        ReportPage reportPage = new ReportPage(createDriver());
        ErrorReportRow row = reportPage.getErrorRowForContext(mockContext);
        assertFalse(row.isDetailVisible());

        // when
        row.toggleDetail();

        // then
        assertTrue(row.isDetailVisible());
    }

    @Test
    public void errorRowContainsExceptionDetailsOnError() throws Exception {
        // given
        RouteRunErrorContext mockContext = mock(RouteRunErrorContext.class);
        when(mockContext.getExceptionMessage()).thenReturn("Some exception message");
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object obj = invocation.getArguments()[0];
                if (!(obj instanceof PrintWriter)) {
                    throw new Exception("Expected first and only arg of printStackTrace to be PrintWriter");
                }
                PrintWriter writer = (PrintWriter)obj;
                writer.print("This is a stack trace");
                return null;
            }
        }).when(mockContext).printStackTrace(any(PrintWriter.class));
        List<RouteRunErrorContext> errorContexts = ImmutableList.of(mockContext);
        when(mockJournal.getErrorContexts()).thenReturn(errorContexts);

        // when
        reportWriter.write(mockJournal);

        // then
        ReportPage reportPage = new ReportPage(createDriver());
        ErrorReportRow row = reportPage.getErrorRowForContext(mockContext);
        row.toggleDetail();
        assertThat(row.getErrorMessage(), is("Some exception message"));
        assertThat(row.getStackTrace(), is("This is a stack trace"));
    }

    private HtmlUnitDriver createDriver() throws Exception {
        File file = new File(OUT_FILE);
        HtmlUnitDriver webDriver = new HtmlUnitDriver();
        webDriver.setJavascriptEnabled(true);
        webDriver.navigate().to(file.toURI().toURL());
        return webDriver;
    }

    private static class ReportPage {
        private final HtmlUnitDriver webDriver;

        private ReportPage(HtmlUnitDriver webDriver) {
            this.webDriver = webDriver;
        }

        public int getVulnerabilityCount() {
            // The first <tr> is the header, so subtract one
            return webDriver.findElements(By.xpath(
                    "//table[@id='vulnerabilities']//tr"
            )).size() - 1;
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

        public int getWarningsCount() {
            // The first <tr> is the header, so subtract one
            return webDriver.findElements(By.xpath(
                    "//table[@id='warnings']//tr"
            )).size() - 1;
        }

        public boolean hasWarningForClass(String identifier) {
            return webDriver.findElements(By.xpath(
                    "//table[@id='warnings']//tr//td[1][contains(text(),'"+identifier+"')]"
            )).size() == 1;
        }

        public int getErrorsCount() {
            // The first <tr> is the header, so subtract one
            return webDriver.findElements(By.xpath(
                    "//table[@id='errors']//tr"
            )).size() - 1;
        }

        public boolean hasErrorForContext(RouteRunErrorContext errorContext) {
            return webDriver.findElements(By.cssSelector("#errorContext-"+errorContext.hashCode())).size() == 1;
        }

        public ErrorReportRow getErrorRowForContext(RouteRunErrorContext errorContext) {
            WebElement row = webDriver.findElement(By.cssSelector("#errorContext-"+errorContext.hashCode()));
            WebElement detailsRow = webDriver.findElement(By.cssSelector("#errorContext-details-" + errorContext.hashCode()));
            return new ErrorReportRow(row, detailsRow);
        }
    }

    private static class ErrorReportRow {
        private final WebElement row;
        private final WebElement detailsRow;

        private ErrorReportRow(WebElement row, WebElement detailsRow) {
            this.row = row;
            this.detailsRow = detailsRow;
        }

        public String getPageClass() {
            return row.findElement(By.xpath("//td[1]")).getText().trim();
        }

        public String getTraversalMethod() {
            return row.findElement(By.xpath("//td[2]")).getText().trim();
        }

        public String getTraversalMode() {
            return row.findElement(By.xpath("//td[3]")).getText().trim();
        }

        public String getErrorMessage() {
            return detailsRow.findElement(By.className("message")).getText().trim();
        }

        public String getStackTrace() {
            return detailsRow.findElement(By.tagName("pre")).getText().trim();
        }

        public boolean isDetailVisible() {
            return detailsRow.isDisplayed();
        }

        public void toggleDetail() {
            row.findElement(By.xpath("//td[4]/a")).click();
        }
    }
}
