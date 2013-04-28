package org.xssfinder.routing;

import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class PageDescriptor {
    private final boolean isRoot;
    private final Set<Method> traversalMethods;
    private final Class<?> pageClass;

    public PageDescriptor(Class<?> pageClass) {
        isRoot = pageClass.isAnnotationPresent(CrawlStartPoint.class);
        traversalMethods = getTraversalMethods(pageClass);
        this.pageClass = pageClass;
    }

    private Set<Method> getTraversalMethods(Class<?> pageClass) {
        Set<Method> traversalMethods = new HashSet<Method>();
        for (Method method : pageClass.getMethods()) {
            Class<?> returnType = method.getReturnType();
            if (returnType != this.getClass() && returnType.isAnnotationPresent(Page.class)) {
                traversalMethods.add(method);
            }
        }
        return traversalMethods;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public Set<Method> getTraversalMethods() {
        return traversalMethods;
    }

    public Class<?> getPageClass() {
        return pageClass;
    }
}
