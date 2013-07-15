package org.xssfinder.runner;

import org.xssfinder.CustomSubmitter;
import org.xssfinder.CustomTraverser;
import org.xssfinder.remote.TraversalMode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PageTraverser {
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
     * @param method The method from the current page object to the next
     * @param traversalMode The mode in which the traversal should be made - controls which annotations are observed
     * @return A result object containing the page object resulting from the traversal plus any XSS attacks made
     */
    public TraversalResult traverse(Object page, Method method, TraversalMode traversalMode) {
        CustomTraverser customTraverser = null;
        CustomSubmitter customSubmitter = null;
        boolean methodMustHaveNoArgs;
        if (traversalMode == TraversalMode.NORMAL) {
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
            Object newPage;
            Map<String, String> inputIdsToAttackIds = new HashMap<String, String>();
            if (traversalMode == TraversalMode.NORMAL) {
                if (customTraverser != null) {
                    newPage = customTraverser.traverse(page);
                } else {
                    newPage = invokeNoArgsMethod(page, method);
                }
            } else {
                if (customSubmitter != null) {
                    LabelledXssGeneratorImpl generator =
                            labelledXssGeneratorFactory.createLabelledXssGenerator();
                    newPage = customSubmitter.submit(page, generator);
                    inputIdsToAttackIds.putAll(generator.getLabelsToAttackIds());
                } else {
                    newPage = invokeNoArgsMethod(page, method);
                }
            }
            return new TraversalResult(newPage, inputIdsToAttackIds);
        } catch (Exception e) {
            throw new UntraversableException(e);
        }
    }

    private Object invokeNoArgsMethod(Object page, Method method) throws IllegalAccessException, InvocationTargetException {
        method.setAccessible(true);
        return method.invoke(page);
    }
}
