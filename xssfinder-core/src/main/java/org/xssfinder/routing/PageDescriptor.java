package org.xssfinder.routing;

import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;
import org.xssfinder.SubmitAction;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class PageDescriptor {
    private final boolean isRoot;
    private final Set<Method> traversalMethods;
    private final Set<Method> submitMethods;
    private final Class<?> pageClass;

    public PageDescriptor(Class<?> pageClass) {
        isRoot = pageClass.isAnnotationPresent(CrawlStartPoint.class);
        traversalMethods = findTraversalMethods(pageClass);
        submitMethods = findSubmitMethods(traversalMethods);
        this.pageClass = pageClass;
    }

    private Set<Method> findTraversalMethods(Class<?> pageClass) {
        Set<Method> traversalMethods = new HashSet<Method>();
        for (Method method : pageClass.getMethods()) {
            Class<?> returnType = method.getReturnType();
            if (returnType != this.getClass() && returnType.isAnnotationPresent(Page.class)) {
                traversalMethods.add(method);
            }
        }
        return traversalMethods;
    }

    Set<Method> findSubmitMethods(Set<Method> traversalMethods) {
        Set<Method> submitMethods = new HashSet<Method>();
        for (Method traversalMethod : traversalMethods) {
            if (traversalMethod.isAnnotationPresent(SubmitAction.class)) {
                submitMethods.add(traversalMethod);
            }
        }
        return submitMethods;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public Set<Method> getTraversalMethods() {
        return traversalMethods;
    }

    public Set<Method> getSubmitMethods() {
        return submitMethods;
    }

    public Class<?> getPageClass() {
        return pageClass;
    }
}
