package org.xssfinder.routing;

import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.util.HashSet;
import java.util.Set;

/**
 * Description of a page and its corresponding page object
 */
public class PageDescriptor {
    private final Set<MethodDefinition> traversalMethods;
    private final Set<MethodDefinition> submitMethods;
    private final PageDefinition pageDefinition;

    /**
     * @param pageDefinition The class of the page object representing this page
     */
    public PageDescriptor(PageDefinition pageDefinition) {
        this.pageDefinition = pageDefinition;
        traversalMethods = findTraversalMethods(pageDefinition);
        submitMethods = findSubmitMethods(traversalMethods);
    }

    private Set<MethodDefinition> findTraversalMethods(PageDefinition pageDefinition) {
        Set<MethodDefinition> traversalMethods = new HashSet<MethodDefinition>();
        for (MethodDefinition method : pageDefinition.getMethods()) {
           traversalMethods.add(method);
        }
        return traversalMethods;
    }

    /**
     * @param traversalMethods A set of all methods on the page object that traverse to another page
     * @return The subset of traversalMethods which are @SubmitActions
     */
    Set<MethodDefinition> findSubmitMethods(Set<MethodDefinition> traversalMethods) {
        Set<MethodDefinition> submitMethods = new HashSet<MethodDefinition>();
        for (MethodDefinition traversalMethod : traversalMethods) {
            if (traversalMethod.isSubmitAnnotated()) {
                submitMethods.add(traversalMethod);
            }
        }
        return submitMethods;
    }

    /**
     * @return True if this page is the root of a route
     */
    public boolean isRoot() {
        return pageDefinition.isCrawlStartPoint();
    }

    /**
     * @return A set of methods which traverse from one page to another
     */
    public Set<MethodDefinition> getTraversalMethods() {
        return traversalMethods;
    }

    /**
     * @return A set of traversal methods which submit the page
     */
    public Set<MethodDefinition> getSubmitMethods() {
        return submitMethods;
    }

    /**
     * @return The page object definition described by this object
     */
    public PageDefinition getPageDefinition() {
        return pageDefinition;
    }
}
