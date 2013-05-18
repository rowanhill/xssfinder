package org.xssfinder.xss;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class XssDescriptorTest {
    @Test
    public void submitMethodAndInputAreRequiredAndAvailable() throws Exception {
        // given
        XssDescriptor descriptor = new XssDescriptor(Page.class.getMethod("submit"), "input identifier");

        // when
        Method submitMethod = descriptor.getSubmitMethod();
        String inputIdentifier = descriptor.getInputIdentifier();

        // then
        assertThat(submitMethod, is(Page.class.getMethod("submit")));
        assertThat(inputIdentifier, is("input identifier"));
    }

    private static class Page {
        public Page submit() { return null; }
    }
}
