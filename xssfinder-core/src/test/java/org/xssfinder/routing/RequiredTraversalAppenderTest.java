package org.xssfinder.routing;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.*;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequiredTraversalAppenderTest {
    @Mock
    private DjikstraResult mockDjikstraResult;
    @Mock
    private UntraversedSubmitMethodsFinder mockUntraversedSubmitMethodFinder;
    @Mock
    private DjikstraRunner mockDjikstraRunner;

    @Mock
    private Route mockRoute;
    @Mock
    private PageDescriptor mockSomePageDescriptor;
    @Mock
    private PageDescriptor mockOtherPageDescriptor;
    @Mock
    private PageDescriptor mockAnotherPageDescriptor;

    private List<Route> routes;
    private Set<PageDescriptor> pageDescriptors;
    private SetMultimap<PageDescriptor, Method> untraversedSubmitMethodsByDescriptor;

    @InjectMocks
    private RequiredTraversalAppender traversalAppender;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        routes = new ArrayList<Route>();
        routes.add(mockRoute);

        pageDescriptors = new HashSet<PageDescriptor>();
        pageDescriptors.add(mockSomePageDescriptor);
        pageDescriptors.add(mockOtherPageDescriptor);
        pageDescriptors.add(mockAnotherPageDescriptor);

        when(mockSomePageDescriptor.getPageClass()).thenReturn((Class) SomePage.class);
        when(mockOtherPageDescriptor.getPageClass()).thenReturn((Class)SomeOtherPage.class);
        when(mockAnotherPageDescriptor.getPageClass()).thenReturn((Class) YetAnotherPage.class);

        untraversedSubmitMethodsByDescriptor = LinkedHashMultimap.create();
        when(mockUntraversedSubmitMethodFinder.getUntraversedSubmitMethods(routes, pageDescriptors))
                .thenReturn(untraversedSubmitMethodsByDescriptor);

        when(mockRoute.getRootPageClass()).thenReturn((Class)SomePage.class);
    }

    @Test
    public void appendingRequiredTraversalsLeavesRoutesUnmodifiedIfNoSuchTraversals() {
        // when
        List<Route> appendedRoutes = traversalAppender.appendTraversalsToRoutes(routes, pageDescriptors, mockDjikstraResult);

        // then
        assertThat(appendedRoutes, is(routes));
    }

    @Test
    public void untraversedMethodOnLeafNodeAppendsTraversalToRouteEndingInLeafNode() throws Exception {
        // given
        Route mockCloneRoute = mock(Route.class);
        when(mockRoute.clone()).thenReturn(mockCloneRoute);
        Method method = SomePage.class.getMethod("goToSomeOtherPage");
        untraversedSubmitMethodsByDescriptor.put(mockSomePageDescriptor, method);
        when(mockDjikstraResult.isClassLeafNode(SomePage.class)).thenReturn(true);

        // when
        List<Route> appendedRoutes = traversalAppender.appendTraversalsToRoutes(routes, pageDescriptors, mockDjikstraResult);

        // then
        verify(mockCloneRoute).appendTraversal(method, mockOtherPageDescriptor);
        assertThat(appendedRoutes.size(), is(1));
        assertThat(appendedRoutes, hasItem(mockCloneRoute));
        //TODO final traversal of route has custom traverser suppressed
    }

    @Test
    public void multipleUntraversedMethodsOnLeafNodesProduceMultipleAppendedRoutes() throws Exception {
        // given
        when(mockDjikstraResult.isClassLeafNode(SomePage.class)).thenReturn(true);
        Method method = SomePage.class.getMethod("goToSomeOtherPage");
        Method anotherMethod = SomePage.class.getMethod("goToYetAnotherPage");
        untraversedSubmitMethodsByDescriptor.put(mockSomePageDescriptor, method);
        untraversedSubmitMethodsByDescriptor.put(mockSomePageDescriptor, anotherMethod);
        Route mockRouteClone = mock(Route.class);
        Route mockAnotherRouteClone = mock(Route.class);
        when(mockRoute.clone()).thenReturn(mockRouteClone, mockAnotherRouteClone);

        // when
        List<Route> appendedRoutes = traversalAppender.appendTraversalsToRoutes(routes, pageDescriptors, mockDjikstraResult);

        // then
        verify(mockRouteClone).appendTraversal(method, mockOtherPageDescriptor);
        verify(mockAnotherRouteClone).appendTraversal(anotherMethod, mockAnotherPageDescriptor);
        assertThat(appendedRoutes.size(), is(2));
        assertThat(appendedRoutes, hasItems(mockRouteClone, mockAnotherRouteClone));
        //TODO final traversal of routes have custom traverser suppressed
    }

    @Test
    public void untraversedMethodOnNonLeafNodeAddsNewShortestRouteWithAppendedTraversal() throws Exception {
        // given
        DjikstraResult mockDjikstraResult = mock(DjikstraResult.class);
        Method method = SomePage.class.getMethod("goToSomeOtherPage");
        untraversedSubmitMethodsByDescriptor.put(mockSomePageDescriptor, method);
        when(mockDjikstraResult.isClassLeafNode(SomePage.class)).thenReturn(false);
        Route mockNewRoute = mock(Route.class);
        when(mockDjikstraResult.createRouteEndingAtClass(SomePage.class))
                .thenReturn(mockNewRoute);

        // when
        List<Route> appendedRoutes = traversalAppender.appendTraversalsToRoutes(routes, pageDescriptors, mockDjikstraResult);

        // then
        verify(mockNewRoute).appendTraversal(method, mockOtherPageDescriptor);
        assertThat(appendedRoutes.size(), is(1));
        assertThat(appendedRoutes, hasItem(mockNewRoute));
        //TODO final traversal of route has custom traverser suppressed
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class SomePage {
        public SomeOtherPage goToSomeOtherPage() { return null; }
        public YetAnotherPage goToYetAnotherPage() { return null; }
    }

    private static class SomeOtherPage {}

    private static class YetAnotherPage {}
}
