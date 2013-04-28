package org.xssfinder.routing;

import org.xssfinder.CrawlStartPoint;

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
}
