package org.xssfinder.routing;

import org.xssfinder.CrawlStartPoint;
import org.xssfinder.reflection.*;
import org.xssfinder.reflection.InstantiationException;
import org.xssfinder.runner.LifecycleEventException;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * A series of traversals through the page object graph
 */
public class Route {
    private final PageDescriptor rootPageDescriptor;
    private final String url;
    private final Instantiator instantiator;
    private final PageTraversalFactory pageTraversalFactory;
    private PageTraversal pageTraversal;

    public Route(
            PageDescriptor rootPageDescriptor,
            PageTraversal pageTraversal,
            Instantiator instantiator,
            PageTraversalFactory pageTraversalFactory
    ) {
        this.rootPageDescriptor = rootPageDescriptor;
        this.url = rootPageDescriptor.getCrawlStartPointUrl();
        this.instantiator = instantiator;
        this.pageTraversalFactory = pageTraversalFactory;
        this.pageTraversal = pageTraversal;
    }

    public Class<?> getRootPageClass() {
        return rootPageDescriptor.getPageClass();
    }

    public String getUrl() {
        return url;
    }

    public PageTraversal getPageTraversal() {
        return pageTraversal;
    }

    public PageDescriptor getRootPageDescriptor() {
        return rootPageDescriptor;
    }

    public PageTraversal getLastPageTraversal() {
        PageTraversal traversal = getPageTraversal();
        while (traversal != null && traversal.getNextTraversal() != null) {
            traversal = traversal.getNextTraversal();
        }
        return traversal;
    }

    public void appendTraversal(
            Method traversalMethod,
            PageDescriptor pageDescriptor,
            PageTraversal.TraversalMode traversalMode
    ) {
        PageTraversal newTraversal = pageTraversalFactory.createTraversal(
                traversalMethod, pageDescriptor, traversalMode);
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
        if (handlerClass == Object.class) {
            // Object is the default lifecycle handler; it indicates that no handler has been set, so we return null
            return null;
        }
        try {
            return instantiator.instantiate(handlerClass);
        } catch (InstantiationException ex) {
            throw new LifecycleEventException(ex);
        }
    }

    public Set<Method> getTraversedSubmitMethods() {
        Set<Method> usedMethods = new HashSet<Method>();
        PageTraversal traversal = getPageTraversal();
        while (traversal != null && traversal.getMethod() != null) {
            if (traversal.getTraversalMode() == PageTraversal.TraversalMode.SUBMIT && traversal.isSubmit()) {
                usedMethods.add(traversal.getMethod());
            }
            traversal = traversal.getNextTraversal();
        }
        return usedMethods;
    }

    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    public Route clone() {
        PageTraversal traversal = pageTraversal == null ? null : pageTraversal.clone();
        return new Route(rootPageDescriptor, traversal, instantiator, pageTraversalFactory);
    }
}
