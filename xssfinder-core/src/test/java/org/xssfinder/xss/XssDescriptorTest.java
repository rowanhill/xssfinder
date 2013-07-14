package org.xssfinder.xss;

import org.junit.Test;
import org.xssfinder.remote.MethodDefinition;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class XssDescriptorTest {
    @Test
    public void submitMethodAndInputAreRequiredAndAvailable() throws Exception {
        // given
        MethodDefinition mockMethodDefinition = mock(MethodDefinition.class);
        XssDescriptor descriptor = new XssDescriptor(mockMethodDefinition, "input identifier");

        // when
        MethodDefinition submitMethodDefinition = descriptor.getSubmitMethod();
        String inputIdentifier = descriptor.getInputIdentifier();

        // then
        assertThat(submitMethodDefinition, is(mockMethodDefinition));
        assertThat(inputIdentifier, is("input identifier"));
    }
}
