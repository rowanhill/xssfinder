package org.xssfinder.routing;

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
}
