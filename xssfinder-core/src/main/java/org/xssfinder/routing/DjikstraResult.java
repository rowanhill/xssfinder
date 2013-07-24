package org.xssfinder.routing;

import org.xssfinder.remote.PageDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DjikstraResult {
    private final RouteFactory routeFactory;
    private final Map<String, GraphNode> classesToNodes;
    private final Set<GraphNode> leafNodes;

    public DjikstraResult(RouteFactory routeFactory, Map<String, GraphNode> classesToNodes, Set<GraphNode> leafNodes) {
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

    public Route createRouteEndingAtClass(PageDefinition pageClass) {
        GraphNode node = classesToNodes.get(pageClass.getIdentifier());
        return routeFactory.createRouteEndingAtNode(node);
    }

    public Set<GraphNode> getLeafNodes() {
        return leafNodes;
    }
}
