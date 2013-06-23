package org.xssfinder.routing;

import com.google.common.collect.SetMultimap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RequiredTraversalAppender {

    private final UntraversedSubmitMethodsFinder untraversedSubmitMethodsFinder;

    public RequiredTraversalAppender(UntraversedSubmitMethodsFinder untraversedSubmitMethodsFinder) {
        this.untraversedSubmitMethodsFinder = untraversedSubmitMethodsFinder;
    }

    public List<Route> appendTraversalsToRoutes(List<Route> routes, Set<PageDescriptor> pageDescriptors) {
        SetMultimap<PageDescriptor, Method> submitMethodsByPage =
                untraversedSubmitMethodsFinder.getUntraversedSubmitMethods(routes, pageDescriptors);
        return getRoutesAppendedWithUnusedSubmitMethods(routes, submitMethodsByPage, pageDescriptors);
    }

    private List<Route> getRoutesAppendedWithUnusedSubmitMethods(
            List<Route> routes,
            SetMultimap<PageDescriptor, Method> methodsByPage,
            Set<PageDescriptor> pageDescriptors
    ) {
        List<Route> newRoutes = new ArrayList<Route>();
        for (Route route : routes) {
            PageTraversal lastTraversal = route.getLastPageTraversal();
            Class<?> endClass = lastTraversal == null ? route.getRootPageClass() : lastTraversal.getMethod().getReturnType();
            Set<Method> unusedMethods = getUnusedSubmitMethodsOnPage(endClass, methodsByPage);
            if (unusedMethods.isEmpty()) {
                newRoutes.add(route);
            } else {
                for (Method unusedMethod : unusedMethods) {
                    Route augmentedRoute = route.clone();
                    PageDescriptor resultingPageDescriptor = getPageDescriptorForClass(unusedMethod.getReturnType(), pageDescriptors);
                    augmentedRoute.appendTraversalByMethodToPageDescriptor(unusedMethod, resultingPageDescriptor);
                    newRoutes.add(augmentedRoute);
                }
            }
        }
        return newRoutes;
    }

    private Set<Method> getUnusedSubmitMethodsOnPage(
            Class<?> pageClass,
            SetMultimap<PageDescriptor, Method> methodsByPage
    ) {
        for (PageDescriptor descriptor : methodsByPage.keySet()) {
            if (descriptor.getPageClass() == pageClass) {
                return methodsByPage.get(descriptor);
            }
        }
        return Collections.emptySet();
    }

    private PageDescriptor getPageDescriptorForClass(Class<?> pageClass, Set<PageDescriptor> pageDescriptors) {
        for (PageDescriptor pageDescriptor : pageDescriptors) {
            if (pageDescriptor.getPageClass() == pageClass) {
                return pageDescriptor;
            }
        }
        return null;
    }
}
