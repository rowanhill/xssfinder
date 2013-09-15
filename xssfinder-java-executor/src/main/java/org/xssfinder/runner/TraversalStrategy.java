package org.xssfinder.runner;

import org.xssfinder.remote.TUntraversableException;
import org.xssfinder.remote.TraversalMode;

import java.lang.reflect.Method;

public interface TraversalStrategy {
    boolean canSatisfyMethod(Method method, TraversalMode traversalMode);
    TraversalResult traverse(Object page, Method method) throws TUntraversableException;
}
