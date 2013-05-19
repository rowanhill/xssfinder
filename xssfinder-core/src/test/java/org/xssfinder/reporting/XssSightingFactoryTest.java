package org.xssfinder.reporting;

import org.junit.Test;
import org.xssfinder.runner.PageContext;
import org.xssfinder.xss.XssDescriptor;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class XssSightingFactoryTest {
    @Test
    public void createsXssSightingFromPageContextAndXssDescriptor() {
        // given
        PageContext mockPageContext = mock(PageContext.class);
        XssDescriptor mockXssDescriptor = mock(XssDescriptor.class);
        XssSightingFactory factory = new XssSightingFactory();

        // when
        XssSighting sighting = factory.createXssSighting(mockPageContext, mockXssDescriptor);

        // then
        assertThat(sighting, is(notNullValue()));
    }
}
