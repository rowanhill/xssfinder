package org.xssfinder.routing;

import org.xssfinder.reflection.Instantiator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class LeafNodeRouteFactory {
    private final Instantiator instantiator;
    private final PageTraversalFactory pageTraversalFactory;

    public LeafNodeRouteFactory(Instantiator instantiator, PageTraversalFactory pageTraversalFactory) {
        this.instantiator = instantiator;
        this.pageTraversalFactory = pageTraversalFactory;
    }

    List<Route> getRoutesFromLeafNodes(Set<GraphNode> leafNodes) {
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
            if (node.hasPredecessor()) {
                PageTraversal traversal = pageTraversalFactory.createTraversalToNode(node);
                traversal.setNextTraversal(nextTraversal);
                nextTraversal = traversal;
            }
            node = node.getPredecessor();
        }
        return nextTraversal;
    }
}
