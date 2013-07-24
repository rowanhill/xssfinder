package org.xssfinder.routing;

import org.xssfinder.remote.PageDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class GraphNodesFactory {
    Map<String, GraphNode> createNodes(Set<PageDescriptor> pageDescriptors) {
        Map<String, GraphNode> nodes = new HashMap<String, GraphNode>();
        for (PageDescriptor pageDescriptor : pageDescriptors) {
            nodes.put(pageDescriptor.getPageDefinition().getIdentifier(), new GraphNode(pageDescriptor));
        }
        return nodes;
    }
}
