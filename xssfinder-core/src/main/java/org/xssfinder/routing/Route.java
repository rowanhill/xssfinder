package org.xssfinder.routing;

import org.xssfinder.CrawlStartPoint;

import java.lang.reflect.Method;

public class Route {
    private final Class<?> rootPageClass;
    private final String url;
    private PageTraversal pageTraversal;

    public Route(Class<?> rootPageClass, PageTraversal pageTraversal) {
        this.rootPageClass = rootPageClass;
        this.url = rootPageClass.getAnnotation(CrawlStartPoint.class).url();
        this.pageTraversal = pageTraversal;
    }

    public Class<?> getRootPageClass() {
        return rootPageClass;
    }

    public String getUrl() {
        return url;
    }

    public PageTraversal getPageTraversal() {
        return pageTraversal;
    }

    public PageTraversal getLastPageTraversal() {
        PageTraversal traversal = getPageTraversal();
        while (traversal != null && traversal.getNextTraversal() != null) {
            traversal = traversal.getNextTraversal();
        }
        return traversal;
    }

    public void appendTraversalByMethod(Method traversalMethod) {
        PageTraversal newTraversal = new PageTraversal(traversalMethod);
        PageTraversal lastTraversal = getLastPageTraversal();
        if (lastTraversal == null) {
            pageTraversal = newTraversal;
        } else {
            lastTraversal.setNextTraversal(newTraversal);
        }
    }

    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    public Route clone() {
        PageTraversal traversal = pageTraversal == null ? null : pageTraversal.clone();
        return new Route(rootPageClass, traversal);
    }
}
