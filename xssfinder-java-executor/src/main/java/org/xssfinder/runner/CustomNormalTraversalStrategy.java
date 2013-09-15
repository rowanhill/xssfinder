package org.xssfinder.runner;

import org.xssfinder.CustomTraverser;
import org.xssfinder.remote.TraversalMode;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CustomNormalTraversalStrategy implements TraversalStrategy {
    private final CustomTraverserInstantiator traverserInstantiator;

    public CustomNormalTraversalStrategy(CustomTraverserInstantiator traverserInstantiator) {
        this.traverserInstantiator = traverserInstantiator;
    }

    @Override
    public boolean canSatisfyMethod(Method method, TraversalMode traversalMode) {
        if (traversalMode != TraversalMode.NORMAL) {
            return false;
        }
        CustomTraverser customTraverser =  traverserInstantiator.instantiate(method);
        return customTraverser != null;
    }

    @Override
    public TraversalResult traverse(Object page, Method method) {
        CustomTraverser customTraverser =  traverserInstantiator.instantiate(method);
        Map<String, String> inputIdsToAttackIds = new HashMap<String, String>();
        Object newPage = customTraverser.traverse(page);
        return new TraversalResult(newPage, inputIdsToAttackIds);
    }
}
