package org.xssfinder.routing;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

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
    private PageDescriptor mockPageDescriptor;
    @Mock
    private PageTraversalFactory mockPageTraversalFactory;
    @Mock
    private PageTraversal mockNextPageTraversal;

    @Mock
    private PageDefinition mockPageDefinition;
    @Mock
    private MethodDefinition mockCircularMethodDefinition;
    @Mock
    private MethodDefinition mockSubmitMethodDefinition;

    @Before
    public void setUp() {
        when(mockPageDescriptor.getPageDefinition()).thenReturn(mockPageDefinition);

        when(mockCircularMethodDefinition.getReturnType()).thenReturn(mockPageDefinition);
        when(mockSubmitMethodDefinition.getReturnType()).thenReturn(mockPageDefinition);
        when(mockSubmitMethodDefinition.isSubmitAnnotated()).thenReturn(true);
        when(mockPageDefinition.getMethods()).thenReturn(ImmutableSet.of(mockCircularMethodDefinition, mockSubmitMethodDefinition));

        when(mockPageDefinition.isCrawlStartPoint()).thenReturn(true);
        when(mockPageDefinition.getStartPointUrl()).thenReturn(ROOT_PAGE_URL);
    }

    @Test
    public void rootPageClassIsAvailable() {
        // given
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockPageTraversalFactory);

        // when
        PageDefinition rootPageClass = route.getRootPageClass();

        // then
        assertThat(rootPageClass, is(mockPageDefinition));
    }

    @Test
    public void rootPageDescriptorIsAvailable() {
        // given
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockPageTraversalFactory);

        // when
        PageDescriptor descriptor = route.getRootPageDescriptor();

        // then
        assertThat(descriptor, is(mockPageDescriptor));
    }

    @Test
    public void urlIsTakenFromRootPage() {
        // given
        when(mockPageDescriptor.getCrawlStartPointUrl()).thenReturn(ROOT_PAGE_URL);
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockPageTraversalFactory);

        // when
        String url = route.getUrl();

        // then
        assertThat(url, is(ROOT_PAGE_URL));
    }

    @Test
    public void pageTraversalIsAvailable() {
        // given
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockPageTraversalFactory);

        // when
        PageTraversal traversal = route.getPageTraversal();

        // then
        assertThat(traversal, is(mockPageTraversal));
    }

    @Test
    public void lastPageTraversalIsNullIfFirstPageTraversalIsNull() {
        // given
        Route route = new Route(mockPageDescriptor, null, mockPageTraversalFactory);

        // when
        PageTraversal traversal = route.getLastPageTraversal();

        // then
        assertThat(traversal, is(nullValue()));
    }

    @Test
    public void lastPageTraversalIsFirstPageTraversalIfItHasNoSubsequentTraversal() {
        // given
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockPageTraversalFactory);

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
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockPageTraversalFactory);

        // when
        PageTraversal traversal = route.getLastPageTraversal();

        // then
        assertThat(traversal, is(mockPageTraversal2));
    }

    @Test
    public void appendingTraversalToNullTraversalSetsNewTraversalFromRoot() throws Exception {
        // given
        Route route = new Route(mockPageDescriptor, null, mockPageTraversalFactory);
        PageTraversal.TraversalMode traversalMode = PageTraversal.TraversalMode.NORMAL;
        when(mockPageTraversalFactory.createTraversal(mockCircularMethodDefinition, mockPageDescriptor, traversalMode))
                .thenReturn(mockNextPageTraversal);

        // when
        route.appendTraversal(mockCircularMethodDefinition, mockPageDescriptor, traversalMode);

        // then
        assertThat(route.getPageTraversal(), is(mockNextPageTraversal));
    }

    @Test
    public void appendingTraversalToNonNullTraversalSetsNextTraversal() throws Exception {
        // given
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockPageTraversalFactory);
        PageTraversal.TraversalMode traversalMode = PageTraversal.TraversalMode.NORMAL;
        when(mockPageTraversalFactory.createTraversal(mockCircularMethodDefinition, mockPageDescriptor, traversalMode))
                .thenReturn(mockNextPageTraversal);

        // when
        route.appendTraversal(mockCircularMethodDefinition, mockPageDescriptor, traversalMode);

        // then
        verify(mockPageTraversal).setNextTraversal(mockNextPageTraversal);
    }

    @Test
    public void cloneHasSameRootClass() {
        // given
        Route route = new Route(mockPageDescriptor, null, mockPageTraversalFactory);

        // when
        Route clonedRoute = route.clone();

        // then
        assertThat(clonedRoute.getRootPageClass(), is(mockPageDefinition));
    }

    @Test
    public void cloneHasNullTraversalIfOriginalDoes() {
        // given
        Route route = new Route(mockPageDescriptor, null, mockPageTraversalFactory);

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
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockPageTraversalFactory);

        // when
        Route clonedRoute = route.clone();

        // then
        assertThat(clonedRoute.getPageTraversal(), is(mockCloneTraversal));
    }

    @Test
    public void returnsTraversedSubmitMethods() throws Exception {
        // given
        PageTraversal traversal = new PageTraversal(
                mockSubmitMethodDefinition,
                mockPageDescriptor,
                PageTraversal.TraversalMode.SUBMIT);
        Route route = new Route(mockPageDescriptor, traversal, mockPageTraversalFactory);

        // when
        Set<MethodDefinition> submitMethods = route.getTraversedSubmitMethods();

        // then
        Set<MethodDefinition> expectedMethods = ImmutableSet.of(
                mockSubmitMethodDefinition
        );
        assertThat(submitMethods, is(expectedMethods));
    }

    @Test
    public void submitMethodsTraversedInNormalModeNotConsideredTraversed() throws Exception {
        // given
        PageTraversal traversal = new PageTraversal(
                mockSubmitMethodDefinition,
                mockPageDescriptor,
                PageTraversal.TraversalMode.NORMAL);
        Route route = new Route(mockPageDescriptor, traversal, mockPageTraversalFactory);

        // when
        Set<MethodDefinition> submitMethods = route.getTraversedSubmitMethods();

        // then
        Set<MethodDefinition> expectedMethods = ImmutableSet.of(
        );
        assertThat(submitMethods, is(expectedMethods));
    }

    @Test
    public void normalMethodsTraversedInNormalModeNotConsideredTraversedSubmitMethods() throws Exception {
        // given
        PageTraversal traversal = new PageTraversal(
                mockCircularMethodDefinition,
                mockPageDescriptor,
                PageTraversal.TraversalMode.NORMAL);
        Route route = new Route(mockPageDescriptor, traversal, mockPageTraversalFactory);

        // when
        Set<MethodDefinition> submitMethods = route.getTraversedSubmitMethods();

        // then
        Set<MethodDefinition> expectedMethods = ImmutableSet.of(
        );
        assertThat(submitMethods, is(expectedMethods));
    }

    @Test
    public void normalMethodsTraversedInSubmitModeNotConsideredTraversedSubmitMethods() throws Exception {
        // given
        PageTraversal traversal = new PageTraversal(
                mockCircularMethodDefinition,
                mockPageDescriptor,
                PageTraversal.TraversalMode.SUBMIT);
        Route route = new Route(mockPageDescriptor, traversal, mockPageTraversalFactory);

        // when
        Set<MethodDefinition> submitMethods = route.getTraversedSubmitMethods();

        // then
        Set<MethodDefinition> expectedMethods = ImmutableSet.of(
        );
        assertThat(submitMethods, is(expectedMethods));
    }
}
