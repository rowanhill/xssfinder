package org.xssfinder.routing;

import org.xssfinder.reflection.Instantiator;

import java.util.*;

/**
 * A graph of pages in the web site under test
 */
class Graph {
    private final Set<PageDescriptor> pageDescriptors;
    private final Class<?> rootPageClass;
    private final Instantiator instantiator;

    public Graph(Set<PageDescriptor> pageDescriptors, Instantiator instantiator) {
        this.pageDescriptors = pageDescriptors;
        this.rootPageClass = findRootNode(pageDescriptors);
        this.instantiator = instantiator;
    }

    /**
     * @return A list of routes which visit all pages of the graph at least once
     */
    public List<Route> getRoutes() {
        GraphNodesFactory nodesFactory = new GraphNodesFactory();
        DjikstraRunner djikstraRunner = new DjikstraRunner(pageDescriptors, nodesFactory);
        Set<GraphNode> leafNodes = djikstraRunner.findShortestPathsAndReturnLeafNodes(rootPageClass);
        PageTraversalFactory pageTraversalFactory = new PageTraversalFactory();
        RouteComposer routeComposer  = new RouteComposer(instantiator, pageTraversalFactory);
        List<Route> routes = routeComposer.getRoutesFromLeafNodes(leafNodes);
        RequiredTraversalAppender requiredTraversalAppender = new RequiredTraversalAppender(
                new UntraversedSubmitMethodsFinder()
        );
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
