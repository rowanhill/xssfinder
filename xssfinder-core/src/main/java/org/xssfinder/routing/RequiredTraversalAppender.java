package org.xssfinder.routing;

import com.google.common.collect.SetMultimap;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

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
        SetMultimap<PageDescriptor, MethodDefinition> submitMethodsByPage =
                untraversedSubmitMethodsFinder.getUntraversedSubmitMethods(routes, pageDescriptors);
        return getRoutesAppendedWithUnusedSubmitMethods(routes, submitMethodsByPage, pageDescriptors, djikstraResult);
    }

    private List<Route> getRoutesAppendedWithUnusedSubmitMethods(
            List<Route> routes,
            SetMultimap<PageDescriptor, MethodDefinition> methodsByPage,
            Set<PageDescriptor> pageDescriptors,
            DjikstraResult djikstraResult
    ) {
        List<Route> newRoutes = new ArrayList<Route>();
        for (Route route : routes) {
            PageTraversal lastTraversal = route.getLastPageTraversal();
            String endPageIdentifier = lastTraversal == null ?
                    route.getRootPageDefinition().getIdentifier() :
                    lastTraversal.getMethod().getReturnTypeIdentifier();
            Set<MethodDefinition> unusedMethods = getUnusedSubmitMethodsOnPage(endPageIdentifier, methodsByPage);
            if (unusedMethods.isEmpty()) {
                newRoutes.add(route);
            } else {
                for (MethodDefinition unusedMethod : unusedMethods) {
                    Route routeToAugment = route.clone();
                    appendMethodToRouteAndAddToList(unusedMethod, routeToAugment, newRoutes, pageDescriptors);
                }
                methodsByPage.removeAll(getPageDescriptorForClass(endPageIdentifier, pageDescriptors));
            }
        }
        for (Map.Entry<PageDescriptor, MethodDefinition> unusedMethodByPage : methodsByPage.entries()) {
            PageDefinition owningPage = unusedMethodByPage.getKey().getPageDefinition();
            MethodDefinition unusedMethod = unusedMethodByPage.getValue();
            Route routeToAugment = djikstraResult.createRouteEndingAtClass(owningPage);
            appendMethodToRouteAndAddToList(unusedMethod, routeToAugment, newRoutes, pageDescriptors);
        }
        return newRoutes;
    }

    private void appendMethodToRouteAndAddToList(
            MethodDefinition unusedMethod,
            Route routeToAugment,
            List<Route> newRoutes,
            Set<PageDescriptor> pageDescriptors
    ) {
        PageDescriptor resultingPageDescriptor =
                getPageDescriptorForClass(unusedMethod.getReturnTypeIdentifier(), pageDescriptors);
        routeToAugment.appendTraversal(
                unusedMethod, resultingPageDescriptor, PageTraversal.TraversalMode.SUBMIT);
        newRoutes.add(routeToAugment);
    }

    private Set<MethodDefinition> getUnusedSubmitMethodsOnPage(
            String pageDefinitionIdentifier,
            SetMultimap<PageDescriptor, MethodDefinition> methodsByPage
    ) {
        for (PageDescriptor descriptor : methodsByPage.keySet()) {
            if (descriptor.getPageDefinition().getIdentifier().equals(pageDefinitionIdentifier)) {
                return methodsByPage.get(descriptor);
            }
        }
        return Collections.emptySet();
    }

    private PageDescriptor getPageDescriptorForClass(String pageDefinitionIdentifier, Set<PageDescriptor> pageDescriptors) {
        for (PageDescriptor pageDescriptor : pageDescriptors) {
            if (pageDescriptor.getPageDefinition().getIdentifier().equals(pageDefinitionIdentifier)) {
                return pageDescriptor;
            }
        }
        return null;
    }
}
