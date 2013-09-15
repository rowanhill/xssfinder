package org.xssfinder.runner;

import org.xssfinder.remote.TUntraversableException;
import org.xssfinder.remote.TraversalMode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SimpleMethodTraversalStrategy implements TraversalStrategy {
    @Override
    public boolean canSatisfyMethod(Method method, TraversalMode traversalMode) {
        return true;
    }

    @Override
    public TraversalResult traverse(Object page, Method method) throws TUntraversableException {
        method.setAccessible(true);
        try {
            Object newPage = method.invoke(page);
            Map<String, String> inputIdsToAttackIds = new HashMap<String, String>();
            return new TraversalResult(newPage, inputIdsToAttackIds);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unexpected IllegalAccessException", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Unexpected InvocationTargetException", e);
        } catch (IllegalArgumentException e) {
            throw new TUntraversableException("Cannot invoke method with args");
        }
    }
}
