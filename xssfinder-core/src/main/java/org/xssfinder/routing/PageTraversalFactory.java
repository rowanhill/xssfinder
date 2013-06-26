package org.xssfinder.routing;


public class PageTraversalFactory {
    public PageTraversal createTraversalToNode(GraphNode graphNode) {
        return new PageTraversal(
                graphNode.getPredecessorTraversalMethod(),
                graphNode.getPageDescriptor()
        );
    }
}
