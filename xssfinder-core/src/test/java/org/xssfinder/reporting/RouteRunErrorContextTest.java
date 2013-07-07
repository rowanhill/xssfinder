package org.xssfinder.reporting;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.routing.PageDescriptor;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.runner.PageContext;

import java.io.PrintWriter;
import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouteRunErrorContextTest {
    private static final String EXCEPTION_MESSAGE = "Here is a message";
    private static final Class<?> PAGE_CLASS = SomePage.class;

    @Mock
    private Exception mockException;
    @Mock
    private PageTraversal mockTraversal;

    private RouteRunErrorContext errorContext;

    @Before
    public void setUp() {
        when(mockException.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        PageContext mockContext = mock(PageContext.class);
        PageDescriptor mockDescriptor = mock(PageDescriptor.class);
        when(mockContext.getPageDescriptor()).thenReturn(mockDescriptor);
        //noinspection unchecked
        when(mockDescriptor.getPageClass()).thenReturn((Class)PAGE_CLASS);
        when(mockContext.getPageTraversal()).thenReturn(mockTraversal);

        errorContext = new RouteRunErrorContext(mockException, mockContext);
    }

    @Test
    public void errorContextHasExceptionMessage() {
        // when
        String exceptionMessage = errorContext.getExceptionMessage();

        // then
        assertThat(exceptionMessage, is(EXCEPTION_MESSAGE));
    }

    @Test
    public void errorContextHasExceptionStackTrace() {
        // given
        PrintWriter mockWriter = mock(PrintWriter.class);

        // when
        errorContext.printStackTrace(mockWriter);

        // then
        //noinspection ThrowableResultOfMethodCallIgnored
        verify(mockException).printStackTrace(mockWriter);
    }

    @Test
    public void errorContextHasPageClassName() {
        // when
        String pageClass = errorContext.getPageClassName();

        // then
        assertEquals(pageClass, PAGE_CLASS.getCanonicalName());
    }

    @Test
    public void errorContextHasPageTraversalString() throws Exception {
        // given
        Method method = SomePage.class.getMethod("goToSomePage");
        when(mockTraversal.getMethod()).thenReturn(method);

        // when
        String traversalMethodString = errorContext.getPageTraversalMethodString();

        // then
        assertThat(traversalMethodString, is(method.toString()));
    }

    @Test
    public void errorContextHasPageTraversalModeName() {
        // given
        when(mockTraversal.getTraversalMode()).thenReturn(PageTraversal.TraversalMode.SUBMIT);

        // when
        String modeName = errorContext.getTraversalModeName();

        // then
        assertThat(modeName, is(PageTraversal.TraversalMode.SUBMIT.getDescription()));
    }

    private static class SomePage {
        public SomePage goToSomePage() { return null; }
    }
}
