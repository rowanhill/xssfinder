package org.xssfinder.routing;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GraphsFactory {
    public Set<Graph> createGraphs(Set<Class<?>> pageClasses) {
        Map<Class<?>, Set<PageDescriptor>> setMembership = new HashMap<Class<?>, Set<PageDescriptor>>();
        Set<PageDescriptor> pageDescriptors = new HashSet<PageDescriptor>();

        for (Class<?> pageClass : pageClasses) {
            PageDescriptor descriptor = new PageDescriptor(pageClass);
            Set<PageDescriptor> descriptorSet = new HashSet<PageDescriptor>();
            descriptorSet.add(descriptor);
            setMembership.put(pageClass, descriptorSet);
            pageDescriptors.add(descriptor);
        }

        for (PageDescriptor descriptor : pageDescriptors) {
            Set<PageDescriptor> descriptorSet = setMembership.get(descriptor.getPageClass());

            for (Method traversalMethod : descriptor.getTraversalMethods()) {
                Class<?> linkedPageClass = traversalMethod.getReturnType();
                Set<PageDescriptor> linkedDescriptorsSet = setMembership.get(linkedPageClass);
                descriptorSet.addAll(linkedDescriptorsSet);
                for (PageDescriptor linkedDescriptor : linkedDescriptorsSet) {
                    setMembership.put(linkedDescriptor.getPageClass(), descriptorSet);
                }
            }
        }

        Set<Graph> graphs = new HashSet<Graph>();
        Set<Set<PageDescriptor>> setOfLinkedPageClassSets = new HashSet<Set<PageDescriptor>>(setMembership.values());
        for (Set<PageDescriptor> set : setOfLinkedPageClassSets) {
            Graph graph = new Graph(set);
            graphs.add(graph);
        }
        return graphs;
    }
}
