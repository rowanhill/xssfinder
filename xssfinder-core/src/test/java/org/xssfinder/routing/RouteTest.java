package org.xssfinder.routing;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;
import org.xssfinder.SubmitAction;
import org.xssfinder.reflection.*;
import org.xssfinder.reflection.InstantiationException;
import org.xssfinder.runner.LifecycleEventException;

import java.lang.reflect.Method;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RouteTest {
    private static final String ROOT_PAGE_URL = "http://localhost:8080/";

    @Mock
    private PageTraversal mockPageTraversal;
    @Mock
    private Instantiator mockInstantiator;
    @Mock
    private PageDescriptor mockPageDescriptor;
    @Mock
    private PageTraversalFactory mockPageTraversalFactory;
    @Mock
    private PageTraversal mockNextPageTraversal;

    @Before
    public void setUp() {
        //noinspection unchecked
        when(mockPageDescriptor.getPageClass()).thenReturn((Class) RootPage.class);
    }

    @Test
    public void rootPageClassIsAvailable() {
        // given
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockInstantiator, mockPageTraversalFactory);

        // when
        Class<?> rootPageClass = route.getRootPageClass();

        // then
        assertThat(rootPageClass == RootPage.class, is(true));
    }

    @Test
    public void rootPageDescriptorIsAvailable() {
        // given
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockInstantiator, mockPageTraversalFactory);

        // when
        PageDescriptor descriptor = route.getRootPageDescriptor();

        // then
        assertThat(descriptor, is(mockPageDescriptor));
    }

    @Test
    public void urlIsTakenFromRootPage() {
        // given
        when(mockPageDescriptor.getCrawlStartPointUrl()).thenReturn(ROOT_PAGE_URL);
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockInstantiator, mockPageTraversalFactory);

        // when
        String url = route.getUrl();

        // then
        assertThat(url, is(ROOT_PAGE_URL));
    }

    @Test
    public void pageTraversalIsAvailable() {
        // given
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockInstantiator, mockPageTraversalFactory);

        // when
        PageTraversal traversal = route.getPageTraversal();

        // then
        assertThat(traversal, is(mockPageTraversal));
    }

    @Test
    public void lastPageTraversalIsNullIfFirstPageTraversalIsNull() {
        // given
        Route route = new Route(mockPageDescriptor, null, mockInstantiator, mockPageTraversalFactory);

        // when
        PageTraversal traversal = route.getLastPageTraversal();

        // then
        assertThat(traversal, is(nullValue()));
    }

    @Test
    public void lastPageTraversalIsFirstPageTraversalIfItHasNoSubsequentTraversal() {
        // given
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockInstantiator, mockPageTraversalFactory);

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
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockInstantiator, mockPageTraversalFactory);

        // when
        PageTraversal traversal = route.getLastPageTraversal();

        // then
        assertThat(traversal, is(mockPageTraversal2));
    }

    @Test
    public void appendingTraversalToNullTraversalSetsNewTraversalFromRoot() throws Exception {
        // given
        Route route = new Route(mockPageDescriptor, null, mockInstantiator, mockPageTraversalFactory);
        Method method = RootPage.class.getMethod("circularLink");
        PageTraversal.TraversalMode traversalMode = PageTraversal.TraversalMode.NORMAL;
        when(mockPageTraversalFactory.createTraversal(method, mockPageDescriptor, traversalMode))
                .thenReturn(mockNextPageTraversal);

        // when
        route.appendTraversal(method, mockPageDescriptor, traversalMode);

        // then
        assertThat(route.getPageTraversal(), is(mockNextPageTraversal));
    }

    @Test
    public void appendingTraversalToNonNullTraversalSetsNextTraversal() throws Exception {
        // given
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockInstantiator, mockPageTraversalFactory);
        Method method = RootPage.class.getMethod("circularLink");
        PageTraversal.TraversalMode traversalMode = PageTraversal.TraversalMode.NORMAL;
        when(mockPageTraversalFactory.createTraversal(method, mockPageDescriptor, traversalMode))
                .thenReturn(mockNextPageTraversal);

        // when
        route.appendTraversal(method, mockPageDescriptor, traversalMode);

        // then
        verify(mockPageTraversal).setNextTraversal(mockNextPageTraversal);
    }

    @Test
    public void cloneHasSameRootClass() {
        // given
        Route route = new Route(mockPageDescriptor, null, mockInstantiator, mockPageTraversalFactory);

        // when
        Route clonedRoute = route.clone();

        // then
        assertThat(clonedRoute.getRootPageClass() == RootPage.class, is(true));
    }

    @Test
    public void cloneHasNullTraversalIfOriginalDoes() {
        // given
        Route route = new Route(mockPageDescriptor, null, mockInstantiator, mockPageTraversalFactory);

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
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockInstantiator, mockPageTraversalFactory);

        // when
        Route clonedRoute = route.clone();

        // then
        assertThat(clonedRoute.getPageTraversal(), is(mockCloneTraversal));
    }

    @Test
    public void createsLifecycleHandler() throws Exception {
        // given
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockInstantiator, mockPageTraversalFactory);
        LifecycleHandler mockHandler = mock(LifecycleHandler.class);
        when(mockInstantiator.instantiate(LifecycleHandler.class)).thenReturn(mockHandler);

        // when
        Object handler = route.createLifecycleHandler();

        // then
        assertThat(handler, is((Object)mockHandler));
    }

    @Test(expected=LifecycleEventException.class)
    public void throwsExceptionIfCreatingLifecycleHandlerFails() throws Exception {
        // given
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockInstantiator, mockPageTraversalFactory);
        when(mockInstantiator.instantiate(LifecycleHandler.class)).thenThrow(new InstantiationException(null));

        // when
        route.createLifecycleHandler();
    }

    @Test
    public void createsNullLifecycleHandlerIfNoneSpecified() throws Exception {
        // given
        //noinspection unchecked
        when(mockPageDescriptor.getPageClass()).thenReturn((Class) PageWithoutLifecycleHandler.class);
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockInstantiator, mockPageTraversalFactory);

        // when
        Object handler = route.createLifecycleHandler();

        // then
        assertThat(handler, is(nullValue()));
        //noinspection unchecked
        verify(mockInstantiator, never()).instantiate(any(Class.class));
    }

    @Test
    public void returnsTraversedSubmitMethods() throws Exception {
        // given
        PageTraversal traversal = new PageTraversal(
                RootPage.class.getMethod("submit"),
                mockPageDescriptor,
                PageTraversal.TraversalMode.SUBMIT);
        Route route = new Route(mockPageDescriptor, traversal, mockInstantiator, mockPageTraversalFactory);

        // when
        Set<Method> submitMethods = route.getTraversedSubmitMethods();

        // then
        Set<Method> expectedMethods = ImmutableSet.of(
                RootPage.class.getMethod("submit")
        );
        assertThat(submitMethods, is(expectedMethods));
    }

    @Test
    public void submitMethodsTraversedInNormalModeNotConsideredTraversed() throws Exception {
        // given
        PageTraversal traversal = new PageTraversal(
                RootPage.class.getMethod("submit"),
                mockPageDescriptor,
                PageTraversal.TraversalMode.NORMAL);
        Route route = new Route(mockPageDescriptor, traversal, mockInstantiator, mockPageTraversalFactory);

        // when
        Set<Method> submitMethods = route.getTraversedSubmitMethods();

        // then
        Set<Method> expectedMethods = ImmutableSet.of(
        );
        assertThat(submitMethods, is(expectedMethods));
    }

    @Test
    public void normalMethodsTraversedInNormalModeNotConsideredTraversedSubmitMethods() throws Exception {
        // given
        PageTraversal traversal = new PageTraversal(
                RootPage.class.getMethod("circularLink"),
                mockPageDescriptor,
                PageTraversal.TraversalMode.NORMAL);
        Route route = new Route(mockPageDescriptor, traversal, mockInstantiator, mockPageTraversalFactory);

        // when
        Set<Method> submitMethods = route.getTraversedSubmitMethods();

        // then
        Set<Method> expectedMethods = ImmutableSet.of(
        );
        assertThat(submitMethods, is(expectedMethods));
    }

    @Test
    public void normalMethodsTraversedInSubmitModeNotConsideredTraversedSubmitMethods() throws Exception {
        // given
        PageTraversal traversal = new PageTraversal(
                RootPage.class.getMethod("circularLink"),
                mockPageDescriptor,
                PageTraversal.TraversalMode.SUBMIT);
        Route route = new Route(mockPageDescriptor, traversal, mockInstantiator, mockPageTraversalFactory);

        // when
        Set<Method> submitMethods = route.getTraversedSubmitMethods();

        // then
        Set<Method> expectedMethods = ImmutableSet.of(
        );
        assertThat(submitMethods, is(expectedMethods));
    }

    @SuppressWarnings("UnusedDeclaration")
    @Page
    @CrawlStartPoint(url=ROOT_PAGE_URL, lifecycleHandler=LifecycleHandler.class)
    private static class RootPage {
        public RootPage circularLink() { return null; }
        @SubmitAction
        public RootPage submit() { return null; }
    }

    private static class LifecycleHandler {}

    @SuppressWarnings("UnusedDeclaration")
    @CrawlStartPoint(url=ROOT_PAGE_URL)
    private static class PageWithoutLifecycleHandler {

    }
}
