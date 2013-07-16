package org.xssfinder.routing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

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
    @Mock
    private PageDefinition mockOtherPageDefinition;
    @Mock
    private PageDefinition mockYetAnotherPageDefinition;
    @Mock
    private PageDefinition mockSomePageDefinition;
    @Mock
    private MethodDefinition mockGoToSomeOtherPageMethodDefinition;
    @Mock
    private MethodDefinition mockGoToYetAnotherPageMethodDefinition;

    private List<Route> routes;
    private Set<PageDescriptor> pageDescriptors;
    private SetMultimap<PageDescriptor, MethodDefinition> untraversedSubmitMethodsByDescriptor;

    @InjectMocks
    private RequiredTraversalAppender traversalAppender;


    @SuppressWarnings("UnusedDeclaration")
    private static class SomePage {
        public SomeOtherPage goToSomeOtherPage() { return null; }
        public YetAnotherPage goToYetAnotherPage() { return null; }
    }

    private static class SomeOtherPage {}

    private static class YetAnotherPage {}

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        routes = new ArrayList<Route>();
        routes.add(mockRoute);

        pageDescriptors = new HashSet<PageDescriptor>();
        pageDescriptors.add(mockSomePageDescriptor);
        pageDescriptors.add(mockOtherPageDescriptor);
        pageDescriptors.add(mockAnotherPageDescriptor);

        when(mockSomePageDefinition.getIdentifier()).thenReturn("SomePage");
        when(mockOtherPageDefinition.getIdentifier()).thenReturn("OtherPage");
        when(mockYetAnotherPageDefinition.getIdentifier()).thenReturn("AnotherPage");

        when(mockSomePageDescriptor.getPageDefinition()).thenReturn(mockSomePageDefinition);
        when(mockOtherPageDescriptor.getPageDefinition()).thenReturn(mockOtherPageDefinition);
        when(mockAnotherPageDescriptor.getPageDefinition()).thenReturn(mockYetAnotherPageDefinition);

        when(mockGoToSomeOtherPageMethodDefinition.getReturnTypeIdentifier()).thenReturn("OtherPage");
        when(mockGoToYetAnotherPageMethodDefinition.getReturnTypeIdentifier()).thenReturn("AnotherPage");
        when(mockSomePageDefinition.getMethods()).thenReturn(ImmutableSet.of(
                mockGoToSomeOtherPageMethodDefinition, mockGoToYetAnotherPageMethodDefinition
        ));

        untraversedSubmitMethodsByDescriptor = LinkedHashMultimap.create();
        when(mockUntraversedSubmitMethodFinder.getUntraversedSubmitMethods(routes, pageDescriptors))
                .thenReturn(untraversedSubmitMethodsByDescriptor);

        when(mockRoute.getRootPageClass()).thenReturn(mockSomePageDefinition);
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
        untraversedSubmitMethodsByDescriptor.put(mockSomePageDescriptor, mockGoToSomeOtherPageMethodDefinition);

        // when
        List<Route> appendedRoutes = traversalAppender.appendTraversalsToRoutes(routes, pageDescriptors, mockDjikstraResult);

        // then
        verify(mockCloneRoute).appendTraversal(
                mockGoToSomeOtherPageMethodDefinition,
                mockOtherPageDescriptor,
                PageTraversal.TraversalMode.SUBMIT
        );
        assertThat(appendedRoutes.size(), is(1));
        assertThat(appendedRoutes, hasItem(mockCloneRoute));
    }

    @Test
    public void multipleUntraversedMethodsOnLeafNodesProduceMultipleAppendedRoutes() throws Exception {
        // given
        untraversedSubmitMethodsByDescriptor.put(mockSomePageDescriptor, mockGoToSomeOtherPageMethodDefinition);
        untraversedSubmitMethodsByDescriptor.put(mockSomePageDescriptor, mockGoToYetAnotherPageMethodDefinition);
        Route mockRouteClone = mock(Route.class);
        Route mockAnotherRouteClone = mock(Route.class);
        when(mockRoute.clone()).thenReturn(mockRouteClone, mockAnotherRouteClone);

        // when
        List<Route> appendedRoutes = traversalAppender.appendTraversalsToRoutes(routes, pageDescriptors, mockDjikstraResult);

        // then
        verify(mockRouteClone).appendTraversal(
                mockGoToSomeOtherPageMethodDefinition,
                mockOtherPageDescriptor,
                PageTraversal.TraversalMode.SUBMIT
        );
        verify(mockAnotherRouteClone).appendTraversal(
                mockGoToYetAnotherPageMethodDefinition,
                mockAnotherPageDescriptor,
                PageTraversal.TraversalMode.SUBMIT
        );
        assertThat(appendedRoutes.size(), is(2));
        assertThat(appendedRoutes, hasItems(mockRouteClone, mockAnotherRouteClone));
    }

    @Test
    public void untraversedMethodOnNonLeafNodeAddsNewShortestRouteWithAppendedTraversal() throws Exception {
        // given
        PageTraversal mockTraversal = mock(PageTraversal.class);
        when(mockRoute.getLastPageTraversal()).thenReturn(mockTraversal);
        when(mockTraversal.getMethod()).thenReturn(mockGoToSomeOtherPageMethodDefinition);
        DjikstraResult mockDjikstraResult = mock(DjikstraResult.class);
        untraversedSubmitMethodsByDescriptor.put(mockSomePageDescriptor, mockGoToYetAnotherPageMethodDefinition);
        Route mockNewRoute = mock(Route.class, "mockNewRoute");
        when(mockDjikstraResult.createRouteEndingAtClass(mockSomePageDefinition))
                .thenReturn(mockNewRoute);

        // when
        List<Route> appendedRoutes = traversalAppender.appendTraversalsToRoutes(routes, pageDescriptors, mockDjikstraResult);

        // then
        verify(mockNewRoute).appendTraversal(
                mockGoToYetAnotherPageMethodDefinition,
                mockAnotherPageDescriptor,
                PageTraversal.TraversalMode.SUBMIT
        );
        assertThat(appendedRoutes.size(), is(2));
        assertThat(appendedRoutes, hasItem(mockRoute));
        assertThat(appendedRoutes, hasItem(mockNewRoute));
    }
}
