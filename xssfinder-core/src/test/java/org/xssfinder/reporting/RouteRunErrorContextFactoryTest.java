package org.xssfinder.reporting;

import org.junit.Test;
import org.xssfinder.runner.PageContext;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RouteRunErrorContextFactoryTest {
    @Test
    public void createsErrorContext() {
        // given
        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        Exception mockException = mock(Exception.class);
        PageContext mockPageContext = mock(PageContext.class);
        RouteRunErrorContextFactory factory = new RouteRunErrorContextFactory();

        // when
        RouteRunErrorContext context = factory.createErrorContext(mockException, mockPageContext);

        // then
        assertThat(context, is(notNullValue()));
    }
}
