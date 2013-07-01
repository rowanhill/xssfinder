package org.xssfinder.routing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DjikstraResultTest {
    @Mock
    private RouteFactory mockRouteFactory;

    private Map<Class<?>, GraphNode> classesToNodes = new HashMap<Class<?>, GraphNode>();
    private Set<GraphNode> leafNodes = new HashSet<GraphNode>();

    @Test
    public void routesAreCreatedFromLeafNodes() {
        // given
        DjikstraResult djikstraResult = new DjikstraResult(mockRouteFactory, classesToNodes, leafNodes);
        List<Route> expectedRoutes = new ArrayList<Route>();
        addLeafNodeAndExpectedRoute(expectedRoutes);
        addLeafNodeAndExpectedRoute(expectedRoutes);

        // when
        List<Route> routes = djikstraResult.getRoutesToLeafNodes();

        // then
        Route[] routesArray = new Route[routes.size()];
        routes.toArray(routesArray);
        assertThat(routes, hasItems(routesArray));
    }

    @Test
    public void creatingRouteFromClassDelegatesToRouteFactoryUsingNode() {
        // given
        DjikstraResult djikstraResult = new DjikstraResult(mockRouteFactory, classesToNodes, leafNodes);
        GraphNode mockNode = mock(GraphNode.class);
        classesToNodes.put(SomePage.class, mockNode);
        Route mockRoute = mockRouteCreationForNode(mockNode);

        // when
        Route route = djikstraResult.createRouteEndingAtClass(SomePage.class);

        // then
        assertThat(route, is(mockRoute));
    }

    @Test
    public void canQueryIfClassIsLeafNode() {
        // given
        DjikstraResult djikstraResult = new DjikstraResult(mockRouteFactory, classesToNodes, leafNodes);
        addLeafClass(SomePage.class);
        mapClassToNode(SomeOtherPage.class);

        // when
        boolean isSomePageLeaf = djikstraResult.isClassLeafNode(SomePage.class);
        boolean isSomeOtherPageLeaf = djikstraResult.isClassLeafNode(SomeOtherPage.class);

        // then
        assertThat(isSomePageLeaf, is(true));
        assertThat(isSomeOtherPageLeaf, is(false));
    }

    private void addLeafNodeAndExpectedRoute(List<Route> expectedRoutes) {
        GraphNode mockNode = mock(GraphNode.class);
        leafNodes.add(mockNode);
        expectedRoutes.add(mockRouteCreationForNode(mockNode));
    }

    private Route mockRouteCreationForNode(GraphNode mockNode) {
        Route mockRoute = mock(Route.class);
        when(mockRouteFactory.createRouteEndingAtNode(mockNode)).thenReturn(mockRoute);
        return mockRoute;
    }

    private void addLeafClass(Class<?> pageClass) {
        leafNodes.add(mapClassToNode(pageClass));
    }

    private GraphNode mapClassToNode(Class<?> pageClass) {
        GraphNode mockNode = mock(GraphNode.class);
        classesToNodes.put(pageClass, mockNode);
        return mockNode;
    }

    private static class SomePage {}

    private static class SomeOtherPage {}
}
