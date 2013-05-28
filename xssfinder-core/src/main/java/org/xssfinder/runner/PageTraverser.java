package org.xssfinder.runner;

import org.xssfinder.CustomTraverser;
import org.xssfinder.routing.PageTraversal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class PageTraverser {
    private final CustomTraverserInstantiator traverserInstantiator;

    public PageTraverser(CustomTraverserInstantiator traverserInstantiator) {
        this.traverserInstantiator = traverserInstantiator;
    }

    public Object traverse(Object page, PageTraversal traversal) {
        Method method = traversal.getMethod();
        CustomTraverser customTraverser = traverserInstantiator.instantiate(method);
        if (method.getParameterTypes().length > 0 && customTraverser == null) {
            throw new UntraversableException("Cannot traverse methods that take parameters");
        }
        try {
            if (customTraverser != null) {
                return customTraverser.traverse(page);
            } else {
                return invokeNoArgsMethod(page, method);
            }
        } catch (Exception e) {
            throw new UntraversableException(e);
        }
    }

    private Object invokeNoArgsMethod(Object page, Method method) throws IllegalAccessException, InvocationTargetException {
        method.setAccessible(true);
        return method.invoke(page);
    }
}
