package org.xssfinder.routing;

import java.util.LinkedList;

public class RouteFactory {
    private final PageTraversalFactory pageTraversalFactory;

    public RouteFactory(PageTraversalFactory pageTraversalFactory) {
        this.pageTraversalFactory = pageTraversalFactory;
    }

    public Route createRouteEndingAtNode(GraphNode graphNode) {
        LinkedList<GraphNode> routeNodes = new LinkedList<GraphNode>();
        PageTraversal nextTraversal = buildPageTraversalsEndingInNode(graphNode, routeNodes);
        GraphNode firstNode = routeNodes.getFirst();
        return new Route(firstNode.getPageDescriptor(), nextTraversal, pageTraversalFactory);
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
