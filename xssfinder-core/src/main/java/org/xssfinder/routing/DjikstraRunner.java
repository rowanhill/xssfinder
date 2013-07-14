package org.xssfinder.routing;

import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.util.*;

class DjikstraRunner {
    private final GraphNodesFactory graphNodesFactory;
    private final DjikstraResultFactory djikstraResultFactory;

    DjikstraRunner(GraphNodesFactory graphNodesFactory, DjikstraResultFactory djikstraResultFactory) {
        this.graphNodesFactory = graphNodesFactory;
        this.djikstraResultFactory = djikstraResultFactory;
    }

    DjikstraResult computeShortestPaths(PageDefinition rootPageClass, Set<PageDescriptor> pageDescriptors) {
        Map<PageDefinition, GraphNode> nodes = graphNodesFactory.createNodes(pageDescriptors);

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
            for (MethodDefinition traversalMethod : nearestNode.getTraversalMethods()) {
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

        return djikstraResultFactory.createResult(nodes, leafNodes);
    }

    private static class NodeDistanceComparator implements Comparator<GraphNode> {
        @Override
        public int compare(GraphNode node1, GraphNode node2) {
            return new Integer(node1.getDistance()).compareTo(node2.getDistance());
        }
    }
}
