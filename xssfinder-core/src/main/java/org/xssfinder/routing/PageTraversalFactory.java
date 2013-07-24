package org.xssfinder.routing;

import org.xssfinder.remote.MethodDefinition;

public class PageTraversalFactory {
    public PageTraversal createTraversalToNode(GraphNode graphNode, PageTraversal.TraversalMode traversalMode) {
        return new PageTraversal(
                graphNode.getPredecessorTraversalMethod(),
                graphNode.getPageDescriptor(),
                traversalMode
        );
    }

    public PageTraversal createTraversal(
            MethodDefinition method,
            PageDescriptor pageDescriptor,
            PageTraversal.TraversalMode traversalMode
    ) {
        return new PageTraversal(
                method,
                pageDescriptor,
                traversalMode
        );
    }
}
