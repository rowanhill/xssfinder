package org.xssfinder.routing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DjikstraResult {
    private final RouteFactory routeFactory;
    private final Map<Class<?>, GraphNode> classesToNodes;
    private final Set<GraphNode> leafNodes;

    public DjikstraResult(RouteFactory routeFactory, Map<Class<?>, GraphNode> classesToNodes, Set<GraphNode> leafNodes) {
        this.routeFactory = routeFactory;
        this.classesToNodes = classesToNodes;
        this.leafNodes = leafNodes;
    }

    public List<Route> getRoutesToLeafNodes() {
        List<Route> routes = new ArrayList<Route>();
        for (GraphNode leafNode : leafNodes) {
            routes.add(routeFactory.createRouteEndingAtNode(leafNode));
        }
        return routes;
    }

    public Route createRouteEndingAtClass(Class<?> pageClass) {
        GraphNode node = classesToNodes.get(pageClass);
        return routeFactory.createRouteEndingAtNode(node);
    }

    public boolean isClassLeafNode(Class<?> pageClass) {
        GraphNode node = classesToNodes.get(pageClass);
        return leafNodes.contains(node);
    }

    public Set<GraphNode> getLeafNodes() {
        return leafNodes;
    }
}
