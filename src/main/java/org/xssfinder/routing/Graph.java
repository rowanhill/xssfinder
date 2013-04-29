package org.xssfinder.routing;

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
        return getRoutesFromLeafNodes(leafNodes);
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
            Route route = new Route(firstNode.getPageClass());
            route.setPageTraversal(nextTraversal);
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
