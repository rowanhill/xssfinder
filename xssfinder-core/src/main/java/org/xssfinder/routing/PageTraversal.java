package org.xssfinder.routing;

import org.xssfinder.remote.MethodDefinition;

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

        public static TraversalMode convertFromThrift(org.xssfinder.remote.TraversalMode thriftTraversalMode) {
            return TraversalMode.valueOf(thriftTraversalMode.name());
        }

        public String getDescription() { return description; }

        public org.xssfinder.remote.TraversalMode convertToThrift() {
            return org.xssfinder.remote.TraversalMode.valueOf(this.name());
        }
    }

    private final MethodDefinition method;
    private final PageDescriptor resultingPageDescriptor;
    private final TraversalMode traversalMode;
    private PageTraversal nextTraversal = null;

    /**
     * @param method The traversal message on a page object
     * @param resultingPageDescriptor The page that results from undertaking this traversal
     * @param traversalMode Traversal mode to use when traversing the method
     */
    public PageTraversal(MethodDefinition method, PageDescriptor resultingPageDescriptor, TraversalMode traversalMode) {
        this.method = method;
        this.resultingPageDescriptor = resultingPageDescriptor;
        this.traversalMode = traversalMode;
    }

    public MethodDefinition getMethod() {
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
        return method.isSubmitAnnotated();
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

    @Override
    public String toString() {
        String childString = nextTraversal == null ? "" : " -> " + nextTraversal.toString();
        return String.format(
                "{%s, %s} -> %s%s",
                this.getMethod().getIdentifier(),
                this.traversalMode.getDescription(),
                this.getMethod().getReturnType().getIdentifier(),
                childString
        );
    }

    public TraversalMode getTraversalMode() {
        return traversalMode;
    }
}
