package org.xssfinder.runner;

import org.xssfinder.CustomSubmitter;
import org.xssfinder.CustomTraverser;
import org.xssfinder.LabelledXssGenerator;
import org.xssfinder.routing.PageTraversal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class PageTraverser {
    private final CustomTraverserInstantiator traverserInstantiator;
    private final CustomSubmitterInstantiator submitterInstantiator;
    private final LabelledXssGeneratorFactory labelledXssGeneratorFactory;

    public PageTraverser(
            CustomTraverserInstantiator traverserInstantiator,
            CustomSubmitterInstantiator submitterInstantiator,
            LabelledXssGeneratorFactory labelledXssGeneratorFactory
    ) {
        this.traverserInstantiator = traverserInstantiator;
        this.submitterInstantiator = submitterInstantiator;
        this.labelledXssGeneratorFactory = labelledXssGeneratorFactory;
    }

    /**
     * Traverse from the given page via the method described by the given traversal, either by the standard traverser
     * if possible or via a custom traverser if instructed.
     *
     * @param page The current page object
     * @param traversal The traversal from the current page object to the next
     * @return The page object resulting from the traversal
     */
    public Object traverse(Object page, PageTraversal traversal) {
        Method method = traversal.getMethod();

        CustomTraverser customTraverser = null;
        CustomSubmitter customSubmitter = null;
        boolean methodMustHaveNoArgs;
        if (traversal.getTraversalMode() == PageTraversal.TraversalMode.NORMAL) {
            customTraverser = traverserInstantiator.instantiate(method);
            methodMustHaveNoArgs = customTraverser == null;
        } else {
            customSubmitter = submitterInstantiator.instantiate(method);
            methodMustHaveNoArgs = customSubmitter == null;
        }

        if (method.getParameterTypes().length > 0 && methodMustHaveNoArgs) {
            throw new UntraversableException("Cannot traverse methods that take parameters");
        }

        try {
            if (traversal.getTraversalMode() == PageTraversal.TraversalMode.NORMAL) {
                if (customTraverser != null) {
                    return customTraverser.traverse(page);
                } else {
                    return invokeNoArgsMethod(page, method);
                }
            } else {
                if (customSubmitter != null) {
                    LabelledXssGenerator generator =
                            labelledXssGeneratorFactory.createLabelledXssGenerator(traversal);
                    return customSubmitter.submit(page, generator);
                } else {
                    return invokeNoArgsMethod(page, method);
                }
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
