package org.xssfinder.routing;


public class PageTraversalFactory {
    public PageTraversal createTraversalToNode(GraphNode graphNode, PageTraversal.TraversalMode traversalMode) {
        return new PageTraversal(
                graphNode.getPredecessorTraversalMethod(),
                graphNode.getPageDescriptor(),
                traversalMode);
    }
}
