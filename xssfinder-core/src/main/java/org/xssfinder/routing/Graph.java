package org.xssfinder.routing;

import com.google.common.collect.SetMultimap;
import org.xssfinder.reflection.Instantiator;

import java.lang.reflect.Method;
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
        Map<Class<?>, GraphNode> nodes = nodesFactory.createNodes(pageDescriptors);
        DjikstraRunner djikstraRunner = new DjikstraRunner();
        Set<GraphNode> leafNodes = djikstraRunner.findShortestPathsAndReturnLeafNodes(rootPageClass, nodes);
        RouteComposer routeComposer  = new RouteComposer();
        List<Route> routes = routeComposer.getRoutesFromLeafNodes(leafNodes, instantiator);
        return appendUntraversedRequiredTraversalsToRoutes(routes, pageDescriptors);
    }

    private List<Route> appendUntraversedRequiredTraversalsToRoutes(List<Route> routes, Set<PageDescriptor> pageDescriptors) {
        UntraversedSubmitMethodsFinder untraversedSubmitMethodsFinder = new UntraversedSubmitMethodsFinder();
        SetMultimap<PageDescriptor, Method> submitMethodsByPage =
                untraversedSubmitMethodsFinder.getUntraversedSubmitMethods(routes, pageDescriptors);
        return getRoutesAppendedWithUnusedSubmitMethods(routes, submitMethodsByPage);
    }

    private List<Route> getRoutesAppendedWithUnusedSubmitMethods(
            List<Route> routes,
            SetMultimap<PageDescriptor, Method> methodsByPage
    ) {
        List<Route> newRoutes = new ArrayList<Route>();
        for (Route route : routes) {
            PageTraversal lastTraversal = route.getLastPageTraversal();
            Class<?> endClass = lastTraversal == null ? route.getRootPageClass() : lastTraversal.getMethod().getReturnType();
            Set<Method> unusedMethods = getUnusedSubmitMethodsOnPage(endClass, methodsByPage);
            if (unusedMethods.isEmpty()) {
                newRoutes.add(route);
            } else {
                for (Method unusedMethod : unusedMethods) {
                    Route augmentedRoute = route.clone();
                    PageDescriptor resultingPageDescriptor = getPageDescriptorForClass(unusedMethod.getReturnType());
                    augmentedRoute.appendTraversalByMethodToPageDescriptor(unusedMethod, resultingPageDescriptor);
                    newRoutes.add(augmentedRoute);
                }
            }
        }
        return newRoutes;
    }

    private Set<Method> getUnusedSubmitMethodsOnPage(Class<?> pageClass, SetMultimap<PageDescriptor, Method> methodsByPage) {
        for (PageDescriptor descriptor : methodsByPage.keySet()) {
            if (descriptor.getPageClass() == pageClass) {
                return methodsByPage.get(descriptor);
            }
        }
        return Collections.emptySet();
    }

    private PageDescriptor getPageDescriptorForClass(Class<?> pageClass) {
        for (PageDescriptor pageDescriptor : pageDescriptors) {
            if (pageDescriptor.getPageClass() == pageClass) {
                return pageDescriptor;
            }
        }
        return null;
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
