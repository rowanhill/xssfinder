package org.xssfinder.runner;

import org.xssfinder.CustomTraverser;
import org.xssfinder.TraverseWith;
import org.xssfinder.reflection.*;
import org.xssfinder.reflection.InstantiationException;

import java.lang.reflect.Method;

/**
 * Creates custom traversers from @TraverseWith annotations
 */
class CustomTraverserInstantiator {
    private final Instantiator instantiator;

    public CustomTraverserInstantiator(Instantiator instantiator) {
        this.instantiator = instantiator;
    }

    public CustomTraverser instantiate(Method method) {
        try {
            TraverseWith annotation = method.getAnnotation(TraverseWith.class);
            if (annotation == null) {
                return null;
            }
            Class<? extends CustomTraverser> customTraverserClass = annotation.value();
            return instantiator.instantiate(customTraverserClass);
        } catch (InstantiationException e) {
            throw new CustomTraverserInstantiationException(e);
        }
    }
}
