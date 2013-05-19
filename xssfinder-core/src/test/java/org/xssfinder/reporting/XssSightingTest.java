package org.xssfinder.reporting;

import org.junit.Test;
import org.xssfinder.xss.XssDescriptor;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class XssSightingTest {
    @Test
    public void vulnerableClassNameIsAvailable() throws Exception {
        // given
        Object mockPageObject = mock(Object.class);
        XssDescriptor mockDescriptor = mock(XssDescriptor.class);
        when(mockDescriptor.getSubmitMethod()).thenReturn(FormPage.class.getMethod("submit"));
        XssSighting sighting = new XssSighting(mockPageObject, mockDescriptor);

        // when
        String vulnerableClassName = sighting.getVulnerableClassName();

        // then
        assertThat(vulnerableClassName, is("org.xssfinder.reporting.XssSightingTest.FormPage"));
    }

    @Test
    public void sightingClassNameIsAvailable() throws Exception {
        // given
        Object dummyPageObject = new ResultsPage();
        XssDescriptor mockDescriptor = mock(XssDescriptor.class);
        when(mockDescriptor.getSubmitMethod()).thenReturn(FormPage.class.getMethod("submit"));
        XssSighting sighting = new XssSighting(dummyPageObject, mockDescriptor);

        // when
        String vulnerableClassName = sighting.getSightingClassName();

        // then
        assertThat(vulnerableClassName, is("org.xssfinder.reporting.XssSightingTest.ResultsPage"));
    }

    @Test
    public void submitMethodNameIsAvailable() throws Exception {
        // given
        Object mockPageObject = mock(Object.class);
        XssDescriptor mockDescriptor = mock(XssDescriptor.class);
        when(mockDescriptor.getSubmitMethod()).thenReturn(FormPage.class.getMethod("submit"));
        XssSighting sighting = new XssSighting(mockPageObject, mockDescriptor);

        // when
        String submitMethodName = sighting.getSubmitMethodName();

        // then
        assertThat(submitMethodName, is("submit"));
    }

    @Test
    public void inputIdentifierIsAvailable() throws Exception {
        // given
        Object mockPageObject = mock(Object.class);
        XssDescriptor mockDescriptor = mock(XssDescriptor.class);
        when(mockDescriptor.getInputIdentifier()).thenReturn("/some/xpath");
        XssSighting sighting = new XssSighting(mockPageObject, mockDescriptor);

        // when
        String inputIdentifier = sighting.getInputIdentifier();

        // then
        assertThat(inputIdentifier, is("/some/xpath"));
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class FormPage {
        public ResultsPage submit() { return null; }
    }

    private static class ResultsPage {

    }
}
