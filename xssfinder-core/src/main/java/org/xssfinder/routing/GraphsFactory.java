package org.xssfinder.routing;

import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GraphsFactory {
    private final DjikstraRunner djikstraRunner;
    private final RequiredTraversalAppender requiredTraversalAppender;

    public GraphsFactory(
            DjikstraRunner djikstraRunner,
            RequiredTraversalAppender requiredTraversalAppender
    ) {
        this.djikstraRunner = djikstraRunner;
        this.requiredTraversalAppender = requiredTraversalAppender;
    }

    public Set<Graph> createGraphs(Set<PageDefinition> pageClasses) {
        Map<PageDefinition, Set<PageDescriptor>> setMembership = new HashMap<PageDefinition, Set<PageDescriptor>>();
        Set<PageDescriptor> pageDescriptors = new HashSet<PageDescriptor>();

        for (PageDefinition pageDefinition : pageClasses) {
            PageDescriptor descriptor = new PageDescriptor(pageDefinition);
            Set<PageDescriptor> descriptorSet = new HashSet<PageDescriptor>();
            descriptorSet.add(descriptor);
            setMembership.put(pageDefinition, descriptorSet);
            pageDescriptors.add(descriptor);
        }

        for (PageDescriptor descriptor : pageDescriptors) {
            Set<PageDescriptor> descriptorSet = setMembership.get(descriptor.getPageDefinition());

            for (MethodDefinition traversalMethod : descriptor.getTraversalMethods()) {
                PageDefinition linkedPageClass = traversalMethod.getReturnType();
                Set<PageDescriptor> linkedDescriptorsSet = setMembership.get(linkedPageClass);
                descriptorSet.addAll(linkedDescriptorsSet);
                for (PageDescriptor linkedDescriptor : linkedDescriptorsSet) {
                    setMembership.put(linkedDescriptor.getPageDefinition(), descriptorSet);
                }
            }
        }

        Set<Graph> graphs = new HashSet<Graph>();
        Set<Set<PageDescriptor>> setOfLinkedPageClassSets = new HashSet<Set<PageDescriptor>>(setMembership.values());
        for (Set<PageDescriptor> set : setOfLinkedPageClassSets) {
            Graph graph = new Graph(set, djikstraRunner, requiredTraversalAppender);
            graphs.add(graph);
        }
        return graphs;
    }
}
