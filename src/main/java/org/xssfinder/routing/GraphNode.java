package org.xssfinder.routing;

import java.util.Set;

public class GraphNode {
    private final PageDescriptor pageDescriptor;
    private int distance;
    private GraphNode predecessor;

    public GraphNode(PageDescriptor pageDescriptor) {
        this.pageDescriptor = pageDescriptor;
        this.distance = Integer.MAX_VALUE;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean isRoot() {
        return pageDescriptor.isRoot();
    }

    public Set<Class<?>> getNeighbours() {
        return pageDescriptor.getLinkedPages();
    }

    public GraphNode getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(GraphNode predecessor) {
        this.predecessor = predecessor;
    }

    public Class<?> getPageClass() {
        return pageDescriptor.getPageClass();
    }
}
