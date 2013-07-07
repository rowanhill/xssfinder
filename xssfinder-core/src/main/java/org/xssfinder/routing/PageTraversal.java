package org.xssfinder.routing;

import org.xssfinder.SubmitAction;

import java.lang.reflect.Method;

/**
 * A traversal from one page to another as part of a route
 */
public class PageTraversal {

    public enum TraversalMode {
        NORMAL("Normal"),
        SUBMIT("Submit");

        private final String description;

        private TraversalMode(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }

    private final Method method;
    private final PageDescriptor resultingPageDescriptor;
    private final TraversalMode traversalMode;
    private PageTraversal nextTraversal = null;

    /**
     * @param method The traversal message on a page object
     * @param resultingPageDescriptor The page that results from undertaking this traversal
     * @param traversalMode Traversal mode to use when traversing the method
     */
    public PageTraversal(Method method, PageDescriptor resultingPageDescriptor, TraversalMode traversalMode) {
        this.method = method;
        this.resultingPageDescriptor = resultingPageDescriptor;
        this.traversalMode = traversalMode;
    }

    public Method getMethod() {
        return method;
    }

    public PageDescriptor getResultingPageDescriptor() {
        return resultingPageDescriptor;
    }

    /**
     * @return The next traversal in the route
     */
    public PageTraversal getNextTraversal() {
        return nextTraversal;
    }

    public void setNextTraversal(PageTraversal nextTraversal) {
        this.nextTraversal = nextTraversal;
    }

    public boolean isSubmit() {
        return method.isAnnotationPresent(SubmitAction.class);
    }

    @Override
    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException", "CloneDoesntCallSuperClone"})
    public PageTraversal clone() {
        PageTraversal clone = new PageTraversal(this.method, this.resultingPageDescriptor, traversalMode);
        if (this.nextTraversal != null) {
            clone.setNextTraversal(this.nextTraversal.clone());
        }
        return clone;
    }

    public TraversalMode getTraversalMode() {
        return traversalMode;
    }
}
