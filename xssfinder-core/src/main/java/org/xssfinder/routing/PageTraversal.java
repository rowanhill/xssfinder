package org.xssfinder.routing;

import org.xssfinder.SubmitAction;

import java.lang.reflect.Method;

/**
 * A traversal from one page to another as part of a route
 */
public class PageTraversal {
    private final Method method;
    private final PageDescriptor resultingPageDescriptor;
    private PageTraversal nextTraversal = null;

    /**
     * @param method The traversal message on a page object
     * @param resultingPageDescriptor The page that results from undertaking this traversal
     */
    public PageTraversal(Method method, PageDescriptor resultingPageDescriptor) {
        this.method = method;
        this.resultingPageDescriptor = resultingPageDescriptor;
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
        PageTraversal clone = new PageTraversal(this.method, this.resultingPageDescriptor);
        if (this.nextTraversal != null) {
            clone.setNextTraversal(this.nextTraversal.clone());
        }
        return clone;
    }
}
