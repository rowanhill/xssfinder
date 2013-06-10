package org.xssfinder.routing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class GraphNodesFactory {
    Map<Class<?>, GraphNode> createNodes(Set<PageDescriptor> pageDescriptors) {
        Map<Class<?>, GraphNode> nodes = new HashMap<Class<?>, GraphNode>();
        for (PageDescriptor pageDescriptor : pageDescriptors) {
            nodes.put(pageDescriptor.getPageClass(), new GraphNode(pageDescriptor));
        }
        return nodes;
    }
}
