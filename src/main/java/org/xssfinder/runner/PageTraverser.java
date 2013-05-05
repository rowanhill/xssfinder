package org.xssfinder.runner;

import org.xssfinder.routing.PageTraversal;

import java.lang.reflect.Method;

public class PageTraverser {
    public Object traverse(Object page, PageTraversal traversal) {
        Method method = traversal.getMethod();
        if (method.getParameterTypes().length > 0) {
            throw new UntraversableException("Cannot traverse methods that take parameters");
        }
        try {
            method.setAccessible(true);
            return method.invoke(page);
        } catch (Exception e) {
            throw new UntraversableException(e);
        }
    }
}
