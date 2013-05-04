package org.xssfinder.routing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouteTest {
    private static final String ROOT_PAGE_URL = "http://localhost:8080/";

    @Mock
    private PageTraversal mockPageTraversal;

    @Test
    public void rootPageClassIsAvailable() {
        // given
        Route route = new Route(RootPage.class, mockPageTraversal);

        // when
        Class<?> rootPageClass = route.getRootPageClass();

        // then
        assertThat(rootPageClass == RootPage.class, is(true));
    }

    @Test
    public void urlIsTakenFromRootPage() {
        // given
        Route route = new Route(RootPage.class, mockPageTraversal);

        // when
        String url = route.getUrl();

        // then
        assertThat(url, is(ROOT_PAGE_URL));
    }

    @Test
    public void pageTraversalIsAvailable() {
        // given
        Route route = new Route(RootPage.class, mockPageTraversal);

        // when
        PageTraversal traversal = route.getPageTraversal();

        // then
        assertThat(traversal, is(mockPageTraversal));
    }

    @Test
    public void lastPageTraversalIsNullIfFirstPageTraversalIsNull() {
        // given
        Route route = new Route(RootPage.class, null);

        // when
        PageTraversal traversal = route.getLastPageTraversal();

        // then
        assertThat(traversal, is(nullValue()));
    }

    @Test
    public void lastPageTraversalIsFirstPageTraversalIfItHasNoSubsequentTraversal() {
        // given
        Route route = new Route(RootPage.class, mockPageTraversal);

        // when
        PageTraversal traversal = route.getLastPageTraversal();

        // then
        assertThat(traversal, is(mockPageTraversal));
    }

    @Test
    public void lastPageTraversalWalksDownChainOfTraversals() {
        // given
        PageTraversal mockPageTraversal2 = mock(PageTraversal.class);
        when(mockPageTraversal.getNextTraversal()).thenReturn(mockPageTraversal2);
        Route route = new Route(RootPage.class, mockPageTraversal);

        // when
        PageTraversal traversal = route.getLastPageTraversal();

        // then
        assertThat(traversal, is(mockPageTraversal2));
    }

    @Test
    public void appendingTraversalToNullTraversalSetsNewTraversalFromRoot() throws Exception {
        // given
        Route route = new Route(RootPage.class, null);

        // when
        route.appendTraversalByMethod(RootPage.class.getMethod("circularLink"));

        // then
        assertThat(route.getPageTraversal(), is(not(nullValue())));
        assertThat(route.getPageTraversal().getMethod(), is(RootPage.class.getMethod("circularLink")));
    }

    @Test
    public void appendingTraversalToNonNullTraversalSetsNextTraversal() throws Exception {
        // given
        Route route = new Route(RootPage.class, mockPageTraversal);

        // when
        route.appendTraversalByMethod(RootPage.class.getMethod("circularLink"));

        // then
        verify(mockPageTraversal).setNextTraversal(any(PageTraversal.class));
    }

    @Test
    public void cloneHasSameRootClass() {
        // given
        Route route = new Route(RootPage.class, null);

        // when
        Route clonedRoute = route.clone();

        // then
        assertThat(clonedRoute.getRootPageClass() == RootPage.class, is(true));
    }

    @Test
    public void cloneHasNullTraversalIfOriginalDoes() {
        // given
        Route route = new Route(RootPage.class, null);

        // when
        Route clonedRoute = route.clone();

        // then
        assertThat(clonedRoute.getPageTraversal(), is(nullValue()));
    }

    @Test
    public void cloneHasCloneOfOriginalPageTraversal() {
        // given
        PageTraversal mockCloneTraversal = mock(PageTraversal.class);
        when(mockPageTraversal.clone()).thenReturn(mockCloneTraversal);
        Route route = new Route(RootPage.class, mockPageTraversal);

        // when
        Route clonedRoute = route.clone();

        // then
        assertThat(clonedRoute.getPageTraversal(), is(mockCloneTraversal));
    }

    @SuppressWarnings("UnusedDeclaration")
    @Page
    @CrawlStartPoint(url=ROOT_PAGE_URL)
    private class RootPage {
        public RootPage circularLink() { return null; }
    }
}
