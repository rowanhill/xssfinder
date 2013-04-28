package org.xssfinder.routing;

import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class PageDescriptor {
    private final boolean isRoot;
    private final Set<Class<?>> linkedPages;
    private final Class<?> pageClass;

    public PageDescriptor(Class<?> pageClass) {
        isRoot = pageClass.isAnnotationPresent(CrawlStartPoint.class);
        linkedPages = getLinkedPages(pageClass);
        this.pageClass = pageClass;
    }

    private Set<Class<?>> getLinkedPages(Class<?> pageClass) {
        Set<Class<?>> linkedPages = new HashSet<Class<?>>();
        for (Method method : pageClass.getMethods()) {
            Class<?> returnType = method.getReturnType();
            if (returnType != this.getClass() && returnType.isAnnotationPresent(Page.class)) {
                linkedPages.add(method.getReturnType());
            }
        }
        return linkedPages;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public Set<Class<?>> getLinkedPages() {
        return linkedPages;
    }

    public Class<?> getPageClass() {
        return pageClass;
    }
}
