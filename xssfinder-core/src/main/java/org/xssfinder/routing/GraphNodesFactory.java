package org.xssfinder.routing;

import org.xssfinder.remote.PageDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class GraphNodesFactory {
    Map<PageDefinition, GraphNode> createNodes(Set<PageDescriptor> pageDescriptors) {
        Map<PageDefinition, GraphNode> nodes = new HashMap<PageDefinition, GraphNode>();
        for (PageDescriptor pageDescriptor : pageDescriptors) {
            nodes.put(pageDescriptor.getPageDefinition(), new GraphNode(pageDescriptor));
        }
        return nodes;
    }
}
