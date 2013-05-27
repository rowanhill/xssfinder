package org.xssfinder.routing;

import org.xssfinder.CrawlStartPoint;
import org.xssfinder.reflection.*;
import org.xssfinder.reflection.InstantiationException;
import org.xssfinder.runner.LifecycleEventException;

import java.lang.reflect.Method;

public class Route {
    private final Class<?> rootPageClass;
    private final String url;
    private final Instantiator instantiator;
    private PageTraversal pageTraversal;

    public Route(Class<?> rootPageClass, PageTraversal pageTraversal, Instantiator instantiator) {
        this.rootPageClass = rootPageClass;
        this.url = rootPageClass.getAnnotation(CrawlStartPoint.class).url();
        this.instantiator = instantiator;
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

    public Object createLifecycleHandler() {
        Class<?> pageClass = getRootPageClass();
        CrawlStartPoint startPointAnnotation = pageClass.getAnnotation(CrawlStartPoint.class);
        Class<?> handlerClass = startPointAnnotation.lifecycleHandler();
        try {
            return instantiator.instantiate(handlerClass);
        } catch (InstantiationException ex) {
            throw new LifecycleEventException(ex);
        }
    }

    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    public Route clone() {
        PageTraversal traversal = pageTraversal == null ? null : pageTraversal.clone();
        return new Route(rootPageClass, traversal, instantiator);
    }
}
