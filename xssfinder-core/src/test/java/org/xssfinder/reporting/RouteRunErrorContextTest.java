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
import static org.hamcrest.Matchers.nullValue;
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
    private PageContext mockContext;

    private RouteRunErrorContext errorContext;

    @Before
    public void setUp() {
        when(mockException.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        mockContext = mock(PageContext.class);
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
        when(mockMethodDefinition.getIdentifier()).thenReturn("Mock method");
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

    @Test
    public void pageIdentifierIsNullIfPageContextIsNull() {
        // given
        errorContext = new RouteRunErrorContext(mockException, null);

        // when
        String pageId = errorContext.getPageIdentifier();

        // then
        assertThat(pageId, is(nullValue()));
    }

    @Test
    public void pageTraversalMethodStringIsNullIfPageContextIsNull() {
        // given
        errorContext = new RouteRunErrorContext(mockException, null);

        // when
        String traversalMethodString = errorContext.getPageTraversalMethodString();

        // then
        assertThat(traversalMethodString, is(nullValue()));
    }

    @Test
    public void traversalModeNameIsNullIfPageContextIsNull() {
        // given
        errorContext = new RouteRunErrorContext(mockException, null);

        // when
        String traversalModeName = errorContext.getTraversalModeName();

        // then
        assertThat(traversalModeName, is(nullValue()));
    }

    @Test
    public void pageTraversalMethodStringIsNullIfPageTraversalIsNull() {
        // given
        when(mockContext.getPageTraversal()).thenReturn(null);

        // when
        String traversalMethodString = errorContext.getPageTraversalMethodString();

        // then
        assertThat(traversalMethodString, is(nullValue()));
    }

    @Test
    public void traversalModeNameIsNullIfPageTraversalIsNull() {
        // given
        when(mockContext.getPageTraversal()).thenReturn(null);

        // when
        String traversalModeName = errorContext.getTraversalModeName();

        // then
        assertThat(traversalModeName, is(nullValue()));
    }
}
