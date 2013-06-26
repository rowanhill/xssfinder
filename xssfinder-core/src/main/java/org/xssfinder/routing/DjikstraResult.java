package org.xssfinder.routing;

import java.util.Map;
import java.util.Set;

public class DjikstraResult {

    private Map<Class<?>, GraphNode> classesToNodes;
    private Set<GraphNode> leafNodes;

    public DjikstraResult(Map<Class<?>, GraphNode> classesToNodes, Set<GraphNode> leafNodes) {
        this.classesToNodes = classesToNodes;
        this.leafNodes = leafNodes;
    }

    public Map<Class<?>, GraphNode> getClassesToNodes() {
        return classesToNodes;
    }

    public Set<GraphNode> getLeafNodes() {
        return leafNodes;
    }
}
