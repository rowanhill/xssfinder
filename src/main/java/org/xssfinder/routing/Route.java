package org.xssfinder.routing;

import org.xssfinder.CrawlStartPoint;

import java.lang.reflect.Method;

public class Route {
    private final Class<?> rootPageClass;
    private final String url;
    private PageTraversal pageTraversal;

    public Route(Class<?> rootPageClass) {
        this.rootPageClass = rootPageClass;
        this.url = rootPageClass.getAnnotation(CrawlStartPoint.class).url();
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

    public void setPageTraversal(PageTraversal pageTraversal) {
        this.pageTraversal = pageTraversal;
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

    @Override
    public Route clone() {
        Route route = new Route(rootPageClass);

        PageTraversal traversal = pageTraversal;
        if (traversal != null) {
            PageTraversal firstClonedTraversal = new PageTraversal(traversal.getMethod());
            PageTraversal currentClonedTraversal = firstClonedTraversal;
            traversal = traversal.getNextTraversal();
            while (traversal != null) {
                PageTraversal newTraversal = new PageTraversal(traversal.getMethod());
                currentClonedTraversal.setNextTraversal(newTraversal);
                currentClonedTraversal = newTraversal;
                traversal = traversal.getNextTraversal();
            }
            route.setPageTraversal(firstClonedTraversal);
        }

        return route;
    }
}
