package org.xssfinder.runner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.routing.PageTraversal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageContextTest {
    @Mock
    private PageTraverser mockPageTraverser;
    @Mock
    private Object mockPage;
    @Mock
    private DriverWrapper mockDriverWrapper;
    @Mock
    private PageTraversal mockPageTraversal;

    @Test
    public void constructsFromPageTraverserAndContextObjects() {
        // when
        new PageContext(mockPageTraverser, mockPage, mockDriverWrapper, mockPageTraversal);
    }

    @Test
    public void pageIsAvailable() {
        // given
        PageContext context = new PageContext(mockPageTraverser, mockPage, mockDriverWrapper, mockPageTraversal);

        // when
        Object page = context.getPage();

        // then
        assertThat(page, is(mockPage));
    }

    @Test
    public void driverWrapperIsAvailable() {
        // given
        PageContext context = new PageContext(mockPageTraverser, mockPage, mockDriverWrapper, mockPageTraversal);

        // when
        DriverWrapper driverWrapper = context.getDriverWrapper();

        // then
        assertThat(driverWrapper, is(mockDriverWrapper));
    }

    @Test
    public void traversalIsAvailable() {
        // given
        PageContext context = new PageContext(mockPageTraverser, mockPage, mockDriverWrapper, mockPageTraversal);

        // when
        PageTraversal traversal = context.getPageTraversal();

        // then
        assertThat(traversal, is(mockPageTraversal));
    }

    @Test
    public void hasNextContextIsTrueIfPageTraversalIsNotNull() {
        // given
        PageContext context = new PageContext(mockPageTraverser, mockPage, mockDriverWrapper, mockPageTraversal);

        // when
        boolean hasNextContext = context.hasNextContext();

        // then
        assertThat(hasNextContext, is(true));
    }

    @Test
    public void hasNextContextIsFalseIfPageTraversalIsNull() {
        // given
        PageContext context = new PageContext(mockPageTraverser, mockPage, mockDriverWrapper, null);

        // when
        boolean hasNextContext = context.hasNextContext();

        // then
        assertThat(hasNextContext, is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void getNextContextThrowsExceptionIfPageTraversalIsNull() {
        // given
        PageContext context = new PageContext(mockPageTraverser, mockPage, mockDriverWrapper, null);

        // when
        context.getNextContext();
    }

    @Test
    public void nextContextPageIsGeneratedByPageTraverser() {
        // given
        Object mockNextPage = mock(Object.class);
        when(mockPageTraverser.traverse(mockPage, mockPageTraversal)).thenReturn(mockNextPage);
        PageContext context = new PageContext(mockPageTraverser, mockPage, mockDriverWrapper, mockPageTraversal);

        // when
        PageContext nextContext = context.getNextContext();
        Object nextPage = nextContext.getPage();

        // then
        assertThat(nextPage, is(mockNextPage));
    }

    @Test
    public void nextButOnePageIsGeneratedFromSecondTraversal() {
        // given
        Object mockNextPage = mock(Object.class);
        Object mockNextButOnePage = mock(Object.class);
        PageTraversal mockNextTraversal = mock(PageTraversal.class);
        when(mockPageTraverser.traverse(mockPage, mockPageTraversal)).thenReturn(mockNextPage);
        when(mockPageTraversal.getNextTraversal()).thenReturn(mockNextTraversal);
        when(mockPageTraverser.traverse(mockNextPage, mockNextTraversal)).thenReturn(mockNextButOnePage);
        PageContext context = new PageContext(mockPageTraverser, mockPage, mockDriverWrapper, mockPageTraversal);

        // when
        PageContext nextContext = context.getNextContext();
        PageContext nextButOneContext = nextContext.getNextContext();
        Object nextButOnePage = nextButOneContext.getPage();

        // then
        assertThat(nextButOnePage, is(mockNextButOnePage));
    }
}
