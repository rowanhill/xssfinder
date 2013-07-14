package org.xssfinder.routing;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.xssfinder.remote.MethodDefinition;

import java.util.*;

class UntraversedSubmitMethodsFinder {

    SetMultimap<PageDescriptor, MethodDefinition> getUntraversedSubmitMethods(
            List<Route> routes,
            Set<PageDescriptor> pageDescriptors
    ) {
        Map<MethodDefinition, PageDescriptor> submitMethodsToPageDescriptor = findSubmitMethodsToPageDescriptor(pageDescriptors);
        for (MethodDefinition usedMethod : findAllUsedSubmitMethods(routes)) {
            submitMethodsToPageDescriptor.remove(usedMethod);
        }
        return invertMap(submitMethodsToPageDescriptor);
    }

    private Map<MethodDefinition, PageDescriptor> findSubmitMethodsToPageDescriptor(Set<PageDescriptor> pageDescriptors) {
        Map<MethodDefinition, PageDescriptor> submitMethodsToPageDescriptor = new HashMap<MethodDefinition, PageDescriptor>();
        for (PageDescriptor descriptor : pageDescriptors) {
            for (MethodDefinition submitMethod : descriptor.getSubmitMethods()) {
                submitMethodsToPageDescriptor.put(submitMethod, descriptor);
            }
        }
        return submitMethodsToPageDescriptor;
    }

    private Set<MethodDefinition> findAllUsedSubmitMethods(List<Route> routes) {
        Set<MethodDefinition> usedMethods = new HashSet<MethodDefinition>();
        for (Route route : routes) {
            usedMethods.addAll(route.getTraversedSubmitMethods());
        }
        return usedMethods;
    }

    private SetMultimap<PageDescriptor, MethodDefinition> invertMap(Map<MethodDefinition, PageDescriptor> map) {
        SetMultimap<PageDescriptor, MethodDefinition> invertedMap = HashMultimap.create();
        for (Map.Entry<MethodDefinition, PageDescriptor> entry : map.entrySet()) {
            invertedMap.put(entry.getValue(), entry.getKey());
        }
        return invertedMap;
    }
}
