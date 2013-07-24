package org.xssfinder.reporting;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;
import org.xssfinder.xss.XssDescriptor;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XssSightingTest {
    @Mock
    private XssDescriptor mockDescriptor;
    @Mock
    private PageDefinition mockPageDefinition;
    @Mock
    private MethodDefinition mockMethodDefinition;

    @Test
    public void vulnerableClassNameIsAvailable() throws Exception {
        // given
        when(mockMethodDefinition.getOwningTypeIdentifier()).thenReturn("pageIdentifier");
        when(mockDescriptor.getSubmitMethod()).thenReturn(mockMethodDefinition);
        XssSighting sighting = new XssSighting(mockPageDefinition, mockDescriptor);

        // when
        String vulnerableClassName = sighting.getVulnerableClassName();

        // then
        assertThat(vulnerableClassName, is("pageIdentifier"));
    }

    @Test
    public void sightingClassNameIsAvailable() throws Exception {
        // given
        when(mockDescriptor.getSubmitMethod()).thenReturn(mockMethodDefinition);
        when(mockMethodDefinition.getIdentifier()).thenReturn("methodIdentifier");
        when(mockPageDefinition.getIdentifier()).thenReturn("pageIdentifier");
        XssSighting sighting = new XssSighting(mockPageDefinition, mockDescriptor);

        // when
        String sightingClassName = sighting.getSightingClassName();

        // then
        assertThat(sightingClassName, is("pageIdentifier"));
    }

    @Test
    public void submitMethodNameIsAvailable() throws Exception {
        // given
        when(mockMethodDefinition.getIdentifier()).thenReturn("methodIdentifier");
        when(mockDescriptor.getSubmitMethod()).thenReturn(mockMethodDefinition);
        XssSighting sighting = new XssSighting(mockPageDefinition, mockDescriptor);

        // when
        String submitMethodName = sighting.getSubmitMethodName();

        // then
        assertThat(submitMethodName, is("methodIdentifier"));
    }

    @Test
    public void inputIdentifierIsAvailable() throws Exception {
        // given
        when(mockDescriptor.getInputIdentifier()).thenReturn("/some/xpath");
        XssSighting sighting = new XssSighting(mockPageDefinition, mockDescriptor);

        // when
        String inputIdentifier = sighting.getInputIdentifier();

        // then
        assertThat(inputIdentifier, is("/some/xpath"));
    }
}
