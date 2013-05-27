package org.xssfinder.runner;

import org.xssfinder.CustomTraverser;
import org.xssfinder.TraverseWith;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CustomTraverserInstantiator {
    public CustomTraverser instantiate(Method method) {
        try {
            TraverseWith annotation = method.getAnnotation(TraverseWith.class);
            if (annotation == null) {
                return null;
            }
            Class<? extends CustomTraverser> customTraverserClass = annotation.value();
            Constructor<? extends CustomTraverser> constructor = customTraverserClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new CustomTraverserInstantiationException(e);
        } catch (InvocationTargetException e) {
            throw new CustomTraverserInstantiationException(e);
        } catch (InstantiationException e) {
            throw new CustomTraverserInstantiationException(e);
        } catch (IllegalAccessException e) {
            throw new CustomTraverserInstantiationException(e);
        }
    }
}
