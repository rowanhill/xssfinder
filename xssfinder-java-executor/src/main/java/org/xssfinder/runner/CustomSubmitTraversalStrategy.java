package org.xssfinder.runner;

import org.xssfinder.CustomSubmitter;
import org.xssfinder.remote.TraversalMode;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CustomSubmitTraversalStrategy implements TraversalStrategy {
    private final CustomSubmitterInstantiator submitterInstantiator;
    private final LabelledXssGeneratorFactory labelledXssGeneratorFactory;

    public CustomSubmitTraversalStrategy(CustomSubmitterInstantiator submitterInstantiator, LabelledXssGeneratorFactory labelledXssGeneratorFactory) {
        this.submitterInstantiator = submitterInstantiator;
        this.labelledXssGeneratorFactory = labelledXssGeneratorFactory;
    }

    @Override
    public boolean canSatisfyMethod(Method method, TraversalMode traversalMode) {
        if (traversalMode != TraversalMode.SUBMIT) {
            return false;
        }
        CustomSubmitter customSubmitter = submitterInstantiator.instantiate(method);
        return customSubmitter != null;
    }

    @Override
    public TraversalResult traverse(Object page, Method method) {
        CustomSubmitter customSubmitter = submitterInstantiator.instantiate(method);
        Map<String, String> inputIdsToAttackIds = new HashMap<String, String>();
        LabelledXssGeneratorImpl generator = labelledXssGeneratorFactory.createLabelledXssGenerator();
        Object newPage = customSubmitter.submit(page, generator);
        inputIdsToAttackIds.putAll(generator.getLabelsToAttackIds());
        return new TraversalResult(newPage, inputIdsToAttackIds);
    }
}
