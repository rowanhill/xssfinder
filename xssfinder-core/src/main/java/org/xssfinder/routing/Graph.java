package org.xssfinder.routing;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.xssfinder.SubmitAction;

import java.lang.reflect.Method;
import java.util.*;

public class Graph {
    private final Set<PageDescriptor> pageDescriptors;
    private final Class<?> rootPageClass;

    public Graph(Set<PageDescriptor> pageDescriptors) {
        this.pageDescriptors = pageDescriptors;
        this.rootPageClass = findRootNode(pageDescriptors);
    }

    public List<Route> getRoutes() {
        Map<Class<?>, GraphNode> nodes = createNodes(pageDescriptors);
        Set<GraphNode> leafNodes = findShortestPathsAndReturnLeafNodes(nodes);
        List<Route> routes = getRoutesFromLeafNodes(leafNodes);
        return appendUntraversedRequiredTraversalsToRoutes(routes, pageDescriptors);
    }

    private List<Route> appendUntraversedRequiredTraversalsToRoutes(List<Route> routes, Set<PageDescriptor> pageDescriptors) {
        Map<Method, PageDescriptor> submitMethodsToPageDescriptor = findSubmitMethodsToPageDescriptor(pageDescriptors);
        for (Method usedMethod : findAllUsedSubmitMethods(routes)) {
            submitMethodsToPageDescriptor.remove(usedMethod);
        }
        return getRoutesAppendedWithUnusedSubmitMethods(routes, invertMap(submitMethodsToPageDescriptor));
    }

    private Map<Method, PageDescriptor> findSubmitMethodsToPageDescriptor(Set<PageDescriptor> pageDescriptors) {
        Map<Method, PageDescriptor> submitMethodsToPageDescriptor = new HashMap<Method, PageDescriptor>();
        for (PageDescriptor descriptor : pageDescriptors) {
            for (Method submitMethod : descriptor.getSubmitMethods()) {
                submitMethodsToPageDescriptor.put(submitMethod, descriptor);
            }
        }
        return submitMethodsToPageDescriptor;
    }

    private Set<Method> findAllUsedSubmitMethods(List<Route> routes) {
        Set<Method> usedMethods = new HashSet<Method>();
        for (Route route : routes) {
            usedMethods.addAll(findAllUsedSubmitMethodsInRoute(route));
        }
        return usedMethods;
    }

    private Set<Method> findAllUsedSubmitMethodsInRoute(Route route) {
        Set<Method> usedMethods = new HashSet<Method>();
        PageTraversal traversal = route.getPageTraversal();
        while (traversal != null && traversal.getMethod() != null) {
            if (traversal.getMethod().isAnnotationPresent(SubmitAction.class)) {
                usedMethods.add(traversal.getMethod());
            }
            traversal = traversal.getNextTraversal();
        }
        return usedMethods;
    }

    private SetMultimap<PageDescriptor, Method> invertMap(Map<Method, PageDescriptor> map) {
        SetMultimap<PageDescriptor, Method> invertedMap = HashMultimap.create();
        for (Map.Entry<Method, PageDescriptor> entry : map.entrySet()) {
            invertedMap.put(entry.getValue(), entry.getKey());
        }
        return invertedMap;
    }

    private List<Route> getRoutesAppendedWithUnusedSubmitMethods(
            List<Route> routes,
            SetMultimap<PageDescriptor, Method> methodsByPage
    ) {
        List<Route> newRoutes = new ArrayList<Route>();
        for (Route route : routes) {
            PageTraversal lastTraversal = route.getLastPageTraversal();
            Class<?> endClass = lastTraversal.getMethod().getReturnType();
            Set<Method> unusedMethods = getUnusedSubmitMethodsOnPage(endClass, methodsByPage);
            if (unusedMethods.isEmpty()) {
                newRoutes.add(route);
            } else {
                for (Method unusedMethod : unusedMethods) {
                    Route augmentedRoute = route.clone();
                    augmentedRoute.appendTraversalByMethod(unusedMethod);
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

    private  Map<Class<?>, GraphNode> createNodes(Set<PageDescriptor> pageDescriptors) {
        Map<Class<?>, GraphNode> nodes = new HashMap<Class<?>, GraphNode>();
        for (PageDescriptor pageDescriptor : pageDescriptors) {
            nodes.put(pageDescriptor.getPageClass(), new GraphNode(pageDescriptor));
        }
        return nodes;
    }

    private Set<GraphNode> findShortestPathsAndReturnLeafNodes(Map<Class<?>, GraphNode> nodes) {
        nodes.get(rootPageClass).setDistance(0);
        PriorityQueue<GraphNode> nodeQueue = new PriorityQueue<GraphNode>(nodes.size(), new NodeDistanceComparator());
        nodeQueue.addAll(nodes.values());

        Set<GraphNode> leafNodes = new HashSet<GraphNode>(nodes.values());
        while (!nodeQueue.isEmpty()) {
            GraphNode nearestNode = nodeQueue.poll();
            if (nearestNode.getDistance() == Integer.MAX_VALUE) {
                throw new DisjointGraphException();
            }
            int nextDistance = nearestNode.getDistance()+1;
            for (Method traversalMethod : nearestNode.getTraversalMethods()) {
                GraphNode neighbour = nodes.get(traversalMethod.getReturnType());
                if (neighbour.getDistance() > nextDistance) {
                    leafNodes.remove(nearestNode);
                    neighbour.setDistance(nextDistance);
                    neighbour.setPredecessor(nearestNode);
                    neighbour.setPredecessorTraversalMethod(traversalMethod);
                    nodeQueue.remove(neighbour);
                    nodeQueue.add(neighbour);
                }
            }
        }
        return leafNodes;
    }

    private List<Route> getRoutesFromLeafNodes(Set<GraphNode> leafNodes) {
        List<Route> routes = new ArrayList<Route>();
        for (GraphNode node : leafNodes) {
            LinkedList<GraphNode> routeNodes = new LinkedList<GraphNode>();
            PageTraversal nextTraversal = buildPageTraversalsEndingInNode(node, routeNodes);
            GraphNode firstNode = routeNodes.getFirst();
            Route route = new Route(firstNode.getPageClass(), nextTraversal);
            routes.add(route);
        }
        return routes;
    }

    private PageTraversal buildPageTraversalsEndingInNode(GraphNode node, LinkedList<GraphNode> routeNodes) {
        PageTraversal nextTraversal = null;
        while (node != null) {
            routeNodes.addFirst(node);
            if (node.getPredecessorTraversalMethod() != null) {
                PageTraversal traversal = new PageTraversal(node.getPredecessorTraversalMethod());
                traversal.setNextTraversal(nextTraversal);
                nextTraversal = traversal;
            }
            node = node.getPredecessor();
        }
        return nextTraversal;
    }

    private class NodeDistanceComparator implements Comparator<GraphNode> {
        @Override
        public int compare(GraphNode node1, GraphNode node2) {
            return new Integer(node1.getDistance()).compareTo(node2.getDistance());
        }
    }
}
