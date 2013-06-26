package org.xssfinder.routing;

import java.util.*;

/**
 * A graph of pages in the web site under test
 */
class Graph {
    private final Set<PageDescriptor> pageDescriptors;
    private final Class<?> rootPageClass;
    private final DjikstraRunner djikstraRunner;
    private final RequiredTraversalAppender requiredTraversalAppender;
    private final RouteComposer routeComposer;

    public Graph(
            Set<PageDescriptor> pageDescriptors,
            DjikstraRunner djikstraRunner,
            RouteComposer routeComposer,
            RequiredTraversalAppender requiredTraversalAppender
    ) {
        this.pageDescriptors = pageDescriptors;
        this.rootPageClass = findRootNode(pageDescriptors);
        this.djikstraRunner = djikstraRunner;
        this.routeComposer = routeComposer;
        this.requiredTraversalAppender = requiredTraversalAppender;
    }

    /**
     * @return A list of routes which visit all pages of the graph at least once
     */
    public List<Route> getRoutes() {
        Set<GraphNode> leafNodes = djikstraRunner.findShortestPathsAndReturnLeafNodes(rootPageClass, pageDescriptors);
        List<Route> routes = routeComposer.getRoutesFromLeafNodes(leafNodes);
        return requiredTraversalAppender.appendTraversalsToRoutes(routes, pageDescriptors);
    }

    private Class<?> findRootNode(Set<PageDescriptor> pageDescriptors) {
        Class<?> root = null;
        for (PageDescriptor pageDescriptor : pageDescriptors) {
            if (pageDescriptor.isRoot()) {
                if (root != null) {
                    throw new MultipleRootPagesFoundException();
                }
                root = pageDescriptor.getPageClass();
            }
        }
        if (root == null) {
            throw new NoRootPageFoundException();
        }
        return root;
    }
}
