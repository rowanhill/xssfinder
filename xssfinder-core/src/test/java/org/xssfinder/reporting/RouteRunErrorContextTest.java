package org.xssfinder.reporting;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;
import org.xssfinder.routing.PageDescriptor;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.runner.PageContext;

import java.io.PrintWriter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.xssfinder.testhelper.MockPageDefinitionBuilder.mockPageDefinition;

@RunWith(MockitoJUnitRunner.class)
public class RouteRunErrorContextTest {
    private static final String EXCEPTION_MESSAGE = "Here is a message";
    private static final String PAGE_NAME = "SomePage";

    @Mock
    private Exception mockException;
    @Mock
    private PageTraversal mockTraversal;
    @Mock
    private MethodDefinition mockMethodDefinition;

    private RouteRunErrorContext errorContext;

    @Before
    public void setUp() {
        when(mockException.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        PageContext mockContext = mock(PageContext.class);
        PageDescriptor mockDescriptor = mock(PageDescriptor.class);
        when(mockContext.getPageDescriptor()).thenReturn(mockDescriptor);
        //noinspection unchecked
        PageDefinition mockPageDefinition = mockPageDefinition()
                .withName(PAGE_NAME)
                .build();
        when(mockDescriptor.getPageDefinition()).thenReturn(mockPageDefinition);
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
    public void errorContextHasPageIdentifier() {
        // when
        String pageClass = errorContext.getPageIdentifier();

        // then
        assertEquals(pageClass, PAGE_NAME);
    }

    @Test
    public void errorContextHasPageTraversalString() throws Exception {
        // given
        when(mockMethodDefinition.toString()).thenReturn("Mock method");
        when(mockTraversal.getMethod()).thenReturn(mockMethodDefinition);

        // when
        String traversalMethodString = errorContext.getPageTraversalMethodString();

        // then
        assertThat(traversalMethodString, is("Mock method"));
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
}
