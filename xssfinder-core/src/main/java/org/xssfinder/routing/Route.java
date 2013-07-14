package org.xssfinder.routing;

import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.util.HashSet;
import java.util.Set;

/**
 * A series of traversals through the page object graph
 */
public class Route {
    private final PageDescriptor rootPageDescriptor;
    private final String url;
    private final PageTraversalFactory pageTraversalFactory;
    private PageTraversal pageTraversal;

    public Route(
            PageDescriptor rootPageDescriptor,
            PageTraversal pageTraversal,
            PageTraversalFactory pageTraversalFactory
    ) {
        this.rootPageDescriptor = rootPageDescriptor;
        this.url = rootPageDescriptor.getCrawlStartPointUrl();
        this.pageTraversalFactory = pageTraversalFactory;
        this.pageTraversal = pageTraversal;
    }

    //qq Rename
    public PageDefinition getRootPageClass() {
        return rootPageDescriptor.getPageDefinition();
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
            MethodDefinition traversalMethod,
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

    public Set<MethodDefinition> getTraversedSubmitMethods() {
        Set<MethodDefinition> usedMethods = new HashSet<MethodDefinition>();
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
        return new Route(rootPageDescriptor, traversal, pageTraversalFactory);
    }
}
