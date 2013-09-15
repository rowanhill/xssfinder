package org.xssfinder.runner;

import com.google.common.collect.ImmutableList;
import org.xssfinder.remote.TUntraversableException;
import org.xssfinder.remote.TWebInteractionException;
import org.xssfinder.remote.TraversalMode;

import java.lang.reflect.Method;
import java.util.List;

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
    public TraversalResult traverse(Object page, Method method, TraversalMode traversalMode)
            throws TUntraversableException, TWebInteractionException
    {
        CustomNormalTraversalStrategy normalStrategy = new CustomNormalTraversalStrategy(this.traverserInstantiator);
        CustomSubmitTraversalStrategy submitStrategy = new CustomSubmitTraversalStrategy(this.submitterInstantiator, this.labelledXssGeneratorFactory);
        SimpleMethodTraversalStrategy methodStrategy = new SimpleMethodTraversalStrategy();

        List<TraversalStrategy> traversalStrategies = ImmutableList.of(normalStrategy, submitStrategy, methodStrategy);

        try {
            for (TraversalStrategy strategy : traversalStrategies) {
                if (strategy.canSatisfyMethod(method, traversalMode)) {
                    return strategy.traverse(page, method);
                }
            }
        } catch (TUntraversableException e) {
            throw e;
        } catch (Exception e) {
            String message;
            if (e.getMessage() == null && e.getCause() != null) {
                message = e.getCause().getMessage();
            } else {
                message = e.getMessage();
            }
            throw new TWebInteractionException("Error when traversing: " + message);
        }

        throw new TUntraversableException("Cannot traverse method");
    }

}
