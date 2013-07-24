package org.xssfinder.routing;

import org.xssfinder.remote.PageDefinition;

import java.util.*;

/**
 * A graph of pages in the web site under test
 */
class Graph {
    private final Set<PageDescriptor> pageDescriptors;
    private final PageDefinition rootPageDefinition;
    private final DjikstraRunner djikstraRunner;
    private final RequiredTraversalAppender requiredTraversalAppender;

    public Graph(
            Set<PageDescriptor> pageDescriptors,
            DjikstraRunner djikstraRunner,
            RequiredTraversalAppender requiredTraversalAppender
    ) {
        this.pageDescriptors = pageDescriptors;
        this.rootPageDefinition = findRootNode(pageDescriptors);
        this.djikstraRunner = djikstraRunner;
        this.requiredTraversalAppender = requiredTraversalAppender;
    }

    /**
     * @return A list of routes which visit all pages of the graph at least once
     */
    public List<Route> getRoutes() {
        DjikstraResult djikstraResult = djikstraRunner.computeShortestPaths(rootPageDefinition, pageDescriptors);
        List<Route> routes = djikstraResult.getRoutesToLeafNodes();
        return requiredTraversalAppender.appendTraversalsToRoutes(routes, pageDescriptors, djikstraResult);
    }

    private PageDefinition findRootNode(Set<PageDescriptor> pageDescriptors) {
        PageDefinition root = null;
        for (PageDescriptor pageDescriptor : pageDescriptors) {
            if (pageDescriptor.isRoot()) {
                if (root != null) {
                    throw new MultipleRootPagesFoundException();
                }
                root = pageDescriptor.getPageDefinition();
            }
        }
        if (root == null) {
            throw new NoRootPageFoundException();
        }
        return root;
    }
}
