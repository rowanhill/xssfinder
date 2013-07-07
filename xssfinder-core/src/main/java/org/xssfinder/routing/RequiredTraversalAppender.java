package org.xssfinder.routing;

import com.google.common.collect.SetMultimap;

import java.lang.reflect.Method;
import java.util.*;

public class RequiredTraversalAppender {
    private final UntraversedSubmitMethodsFinder untraversedSubmitMethodsFinder;

    public RequiredTraversalAppender(UntraversedSubmitMethodsFinder untraversedSubmitMethodsFinder) {
        this.untraversedSubmitMethodsFinder = untraversedSubmitMethodsFinder;
    }

    public List<Route> appendTraversalsToRoutes(
            List<Route> routes,
            Set<PageDescriptor> pageDescriptors,
            DjikstraResult djikstraResult
    ) {
        SetMultimap<PageDescriptor, Method> submitMethodsByPage =
                untraversedSubmitMethodsFinder.getUntraversedSubmitMethods(routes, pageDescriptors);
        return getRoutesAppendedWithUnusedSubmitMethods(routes, submitMethodsByPage, pageDescriptors, djikstraResult);
    }

    private List<Route> getRoutesAppendedWithUnusedSubmitMethods(
            List<Route> routes,
            SetMultimap<PageDescriptor, Method> methodsByPage,
            Set<PageDescriptor> pageDescriptors,
            DjikstraResult djikstraResult
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
                    Route routeToAugment = route.clone();
                    appendMethodToRouteAndAddToList(unusedMethod, routeToAugment, newRoutes, pageDescriptors);
                }
                methodsByPage.removeAll(getPageDescriptorForClass(endClass, pageDescriptors));
            }
        }
        for (Map.Entry<PageDescriptor, Method> unusedMethodByPage : methodsByPage.entries()) {
            Class<?> owningPage = unusedMethodByPage.getKey().getPageClass();
            Method unusedMethod = unusedMethodByPage.getValue();
            Route routeToAugment = djikstraResult.createRouteEndingAtClass(owningPage);
            appendMethodToRouteAndAddToList(unusedMethod, routeToAugment, newRoutes, pageDescriptors);
        }
        return newRoutes;
    }

    private void appendMethodToRouteAndAddToList(
            Method unusedMethod,
            Route routeToAugment,
            List<Route> newRoutes,
            Set<PageDescriptor> pageDescriptors
    ) {
        PageDescriptor resultingPageDescriptor =
                getPageDescriptorForClass(unusedMethod.getReturnType(), pageDescriptors);
        routeToAugment.appendTraversal(
                unusedMethod, resultingPageDescriptor, PageTraversal.TraversalMode.SUBMIT);
        newRoutes.add(routeToAugment);
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
