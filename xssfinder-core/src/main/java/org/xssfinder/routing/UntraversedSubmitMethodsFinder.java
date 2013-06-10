package org.xssfinder.routing;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import java.lang.reflect.Method;
import java.util.*;

class UntraversedSubmitMethodsFinder {

    SetMultimap<PageDescriptor, Method> getUntraversedSubmitMethods(
            List<Route> routes,
            Set<PageDescriptor> pageDescriptors
    ) {
        Map<Method, PageDescriptor> submitMethodsToPageDescriptor = findSubmitMethodsToPageDescriptor(pageDescriptors);
        for (Method usedMethod : findAllUsedSubmitMethods(routes)) {
            submitMethodsToPageDescriptor.remove(usedMethod);
        }
        return invertMap(submitMethodsToPageDescriptor);
    }

    private Map<Method, PageDescriptor> findSubmitMethodsToPageDescriptor(Set<PageDescriptor> pageDescriptors) {
        Map<Method, PageDescriptor> submitMethodsToPageDescriptor = new HashMap<Method, PageDescriptor>();
        for (PageDescriptor descriptor : pageDescriptors) {
            for (Method submitMethod : descriptor.getSubmitMethods()) {
                submitMethodsToPageDescriptor.put(submitMethod, descriptor);
            }
        }
        return submitMethodsToPageDescriptor;
    }

    private Set<Method> findAllUsedSubmitMethods(List<Route> routes) {
        Set<Method> usedMethods = new HashSet<Method>();
        for (Route route : routes) {
            usedMethods.addAll(route.getTraversedSubmitMethods());
        }
        return usedMethods;
    }

    private SetMultimap<PageDescriptor, Method> invertMap(Map<Method, PageDescriptor> map) {
        SetMultimap<PageDescriptor, Method> invertedMap = HashMultimap.create();
        for (Map.Entry<Method, PageDescriptor> entry : map.entrySet()) {
            invertedMap.put(entry.getValue(), entry.getKey());
        }
        return invertedMap;
    }
}
