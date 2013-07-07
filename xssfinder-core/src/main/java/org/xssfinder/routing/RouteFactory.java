package org.xssfinder.routing;

import org.xssfinder.reflection.Instantiator;

import java.util.LinkedList;

public class RouteFactory {
    private final Instantiator instantiator;
    private final PageTraversalFactory pageTraversalFactory;

    public RouteFactory(Instantiator instantiator, PageTraversalFactory pageTraversalFactory) {
        this.instantiator = instantiator;
        this.pageTraversalFactory = pageTraversalFactory;
    }

    public Route createRouteEndingAtNode(GraphNode graphNode) {
        LinkedList<GraphNode> routeNodes = new LinkedList<GraphNode>();
        PageTraversal nextTraversal = buildPageTraversalsEndingInNode(graphNode, routeNodes);
        GraphNode firstNode = routeNodes.getFirst();
        return new Route(firstNode.getPageDescriptor(), nextTraversal, instantiator, pageTraversalFactory);
    }

    private PageTraversal buildPageTraversalsEndingInNode(GraphNode node, LinkedList<GraphNode> routeNodes) {
        PageTraversal nextTraversal = null;
        while (node != null) {
            routeNodes.addFirst(node);
            if (node.hasPredecessor()) {
                PageTraversal traversal = pageTraversalFactory.createTraversalToNode(
                        node,
                        PageTraversal.TraversalMode.NORMAL
                );
                traversal.setNextTraversal(nextTraversal);
                nextTraversal = traversal;
            }
            node = node.getPredecessor();
        }
        return nextTraversal;
    }
}
