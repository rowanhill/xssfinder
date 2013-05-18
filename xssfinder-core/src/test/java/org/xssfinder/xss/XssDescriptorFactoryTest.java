package org.xssfinder.xss;

import org.junit.Test;
import org.xssfinder.routing.PageTraversal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class XssDescriptorFactoryTest {
    @Test
    public void creatingXssDescriptorReturnsXssDescriptor() {
        // given
        PageTraversal mockTraversal = mock(PageTraversal.class);
        XssDescriptorFactory factory = new XssDescriptorFactory();

        // when
        XssDescriptor descriptor = factory.createXssDescriptor(mockTraversal, "body/form[1]/input[1]");

        // then
        assertThat(descriptor, is(not(nullValue())));
    }
}
