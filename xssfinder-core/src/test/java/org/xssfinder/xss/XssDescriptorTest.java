package org.xssfinder.xss;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class XssDescriptorTest {
    @Test
    public void pageAndInputAreRequiredAndAvailable() {
        // given
        XssDescriptor descriptor = new XssDescriptor(Page.class, "input identifier");

        // when
        Class<?> pageClass = descriptor.getPageClass();
        String inputIdentifier = descriptor.getInputIdentifier();

        // then
        assertThat(pageClass == Page.class, is(true));
        assertThat(inputIdentifier, is("input identifier"));
    }

    private static class Page {}
}
