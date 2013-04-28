package org.xssfinder.routing;

import java.util.*;

public class Graph {
    private final Set<PageDescriptor> pageDescriptors;
    private final Class<?> rootPageClass;

    public Graph(Set<PageDescriptor> pageDescriptors) {
        this.pageDescriptors = pageDescriptors;
        this.rootPageClass = findRootNode(pageDescriptors);
    }

    public List<List<Class<?>>> getRoutes() {
        Map<Class<?>, GraphNode> nodes = createNodes(pageDescriptors);
        Set<GraphNode> leafNodes = new HashSet<GraphNode>(nodes.values());

        nodes.get(rootPageClass).setDistance(0);
        PriorityQueue<GraphNode> nodeQueue = new PriorityQueue<GraphNode>(nodes.size(), new NodeDistanceComparator());
        nodeQueue.addAll(nodes.values());

        while (!nodeQueue.isEmpty()) {
            GraphNode nearestNode = nodeQueue.poll();
            if (nearestNode.getDistance() == Integer.MAX_VALUE) {
                throw new DisjointGraphException();
            }
            int nextDistance = nearestNode.getDistance()+1;
            for (Class<?> neighbourClass : nearestNode.getNeighbours()) {
                GraphNode neighbour = nodes.get(neighbourClass);
                if (neighbour.getDistance() > nextDistance) {
                    leafNodes.remove(nearestNode);
                    neighbour.setDistance(nextDistance);
                    neighbour.setPredecessor(nearestNode);
                    nodeQueue.remove(neighbour);
                    nodeQueue.add(neighbour);
                }
            }
        }

        List<List<Class<?>>> routes = new ArrayList<List<Class<?>>>();
        for (GraphNode node : leafNodes) {
            LinkedList<Class<?>> route = new LinkedList<Class<?>>();
            while (node != null) {
                route.addFirst(node.getPageClass());
                node = node.getPredecessor();
            }
            routes.add(route);
        }

        return routes;
    }

    private  Map<Class<?>, GraphNode> createNodes(Set<PageDescriptor> pageDescriptors) {
        Map<Class<?>, GraphNode> nodes = new HashMap<Class<?>, GraphNode>();
        for (PageDescriptor pageDescriptor : pageDescriptors) {
            nodes.put(pageDescriptor.getPageClass(), new GraphNode(pageDescriptor));
        }
        return nodes;
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

    private class NodeDistanceComparator implements Comparator<GraphNode> {
        @Override
        public int compare(GraphNode node1, GraphNode node2) {
            return new Integer(node1.getDistance()).compareTo(node2.getDistance());
        }
    }
}
