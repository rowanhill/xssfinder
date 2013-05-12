package org.xssfinder.xss;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class XssDescriptorFactoryTest {
    @Test
    public void creatingXssDescriptorReturnsXssDescriptor() {
        // given
        XssDescriptorFactory factory = new XssDescriptorFactory();

        // when
        XssDescriptor descriptor = factory.createXssDescriptor(new Object(), "body/form[0]/input[0]");

        // then
        assertThat(descriptor, is(not(nullValue())));
    }
}
