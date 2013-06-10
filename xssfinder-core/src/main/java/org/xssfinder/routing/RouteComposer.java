package org.xssfinder.routing;

import org.xssfinder.reflection.Instantiator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RouteComposer {

    List<Route> getRoutesFromLeafNodes(Set<GraphNode> leafNodes, Instantiator instantiator) {
        List<Route> routes = new ArrayList<Route>();
        for (GraphNode node : leafNodes) {
            LinkedList<GraphNode> routeNodes = new LinkedList<GraphNode>();
            PageTraversal nextTraversal = buildPageTraversalsEndingInNode(node, routeNodes);
            GraphNode firstNode = routeNodes.getFirst();
            Route route = new Route(firstNode.getPageDescriptor(), nextTraversal, instantiator);
            routes.add(route);
        }
        return routes;
    }

    private PageTraversal buildPageTraversalsEndingInNode(GraphNode node, LinkedList<GraphNode> routeNodes) {
        PageTraversal nextTraversal = null;
        while (node != null) {
            routeNodes.addFirst(node);
            if (node.getPredecessorTraversalMethod() != null) {
                PageTraversal traversal = new PageTraversal(
                        node.getPredecessorTraversalMethod(),
                        node.getPageDescriptor()
                );
                traversal.setNextTraversal(nextTraversal);
                nextTraversal = traversal;
            }
            node = node.getPredecessor();
        }
        return nextTraversal;
    }
}
