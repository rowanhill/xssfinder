package org.xssfinder.routing;


import java.lang.reflect.Method;

public class PageTraversalFactory {
    public PageTraversal createTraversalToNode(GraphNode graphNode, PageTraversal.TraversalMode traversalMode) {
        return new PageTraversal(
                graphNode.getPredecessorTraversalMethod(),
                graphNode.getPageDescriptor(),
                traversalMode
        );
    }

    public PageTraversal createTraversal(
            Method method,
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
