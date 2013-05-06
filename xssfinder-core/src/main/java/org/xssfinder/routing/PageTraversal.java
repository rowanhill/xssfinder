package org.xssfinder.routing;

import org.xssfinder.SubmitAction;

import java.lang.reflect.Method;

public class PageTraversal {
    private final Method method;
    private PageTraversal nextTraversal = null;

    public PageTraversal(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
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
        PageTraversal clone = new PageTraversal(this.method);
        if (this.nextTraversal != null) {
            clone.setNextTraversal(this.nextTraversal.clone());
        }
        return clone;
    }
}
