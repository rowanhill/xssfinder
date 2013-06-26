package org.xssfinder.routing;

import java.lang.reflect.Method;
import java.util.Set;

public class GraphNode {
    private final PageDescriptor pageDescriptor;
    private int distance;
    private GraphNode predecessor;
    private Method predecessorTraversalMethod;

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

    public Set<Method> getTraversalMethods() {
        return pageDescriptor.getTraversalMethods();
    }

    public GraphNode getPredecessor() {
        return predecessor;
    }

    public Method getPredecessorTraversalMethod() {
        return predecessorTraversalMethod;
    }

    public void setPredecessor(GraphNode predecessor, Method predecessorTraversalMethod) {
        this.predecessor = predecessor;
        this.predecessorTraversalMethod = predecessorTraversalMethod;
    }

    public Class<?> getPageClass() {
        return pageDescriptor.getPageClass();
    }

    public PageDescriptor getPageDescriptor() {
        return pageDescriptor;
    }

    public boolean hasPredecessor() {
        return this.predecessor != null && this.predecessorTraversalMethod != null;
    }
}
