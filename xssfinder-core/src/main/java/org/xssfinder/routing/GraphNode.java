package org.xssfinder.routing;

import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.lang.reflect.Method;
import java.util.Set;

public class GraphNode {
    private final PageDescriptor pageDescriptor;
    private int distance;
    private GraphNode predecessor;
    private MethodDefinition predecessorTraversalMethod;

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

    public Set<MethodDefinition> getTraversalMethods() {
        return pageDescriptor.getTraversalMethods();
    }

    public GraphNode getPredecessor() {
        return predecessor;
    }

    public MethodDefinition getPredecessorTraversalMethod() {
        return predecessorTraversalMethod;
    }

    public void setPredecessor(GraphNode predecessor, MethodDefinition predecessorTraversalMethod) {
        this.predecessor = predecessor;
        this.predecessorTraversalMethod = predecessorTraversalMethod;
    }

    public PageDefinition getPageDefinition() {
        return pageDescriptor.getPageDefinition();
    }

    public PageDescriptor getPageDescriptor() {
        return pageDescriptor;
    }

    public boolean hasPredecessor() {
        return this.predecessor != null && this.predecessorTraversalMethod != null;
    }
}
