package org.xssfinder.routing;

import org.xssfinder.SubmitAction;

import java.lang.reflect.Method;

public class PageTraversal {
    private final Method method;
    private final PageDescriptor resultingPageDescriptor;
    private PageTraversal nextTraversal = null;

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
