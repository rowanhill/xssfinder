package org.xssfinder.routing;

import java.lang.reflect.Method;
import java.util.*;

class DjikstraRunner {
    private final GraphNodesFactory graphNodesFactory;

    DjikstraRunner(GraphNodesFactory graphNodesFactory) {
        this.graphNodesFactory = graphNodesFactory;
    }

    DjikstraResult computeShortestPaths(Class<?> rootPageClass, Set<PageDescriptor> pageDescriptors) {
        Map<Class<?>, GraphNode> nodes = graphNodesFactory.createNodes(pageDescriptors);

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
                    neighbour.setPredecessor(nearestNode, traversalMethod);
                    nodeQueue.remove(neighbour);
                    nodeQueue.add(neighbour);
                }
            }
        }
        return new DjikstraResult(nodes, leafNodes);
    }

    private static class NodeDistanceComparator implements Comparator<GraphNode> {
        @Override
        public int compare(GraphNode node1, GraphNode node2) {
            return new Integer(node1.getDistance()).compareTo(node2.getDistance());
        }
    }
}
