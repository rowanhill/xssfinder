package org.xssfinder.runner;

import org.xssfinder.AfterRoute;
import org.xssfinder.remote.TLifecycleEventHandlerException;
import org.xssfinder.remote.TWebInteractionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Executes route lifecycle event handlers as appropriate on lifecycle handlers
 */
public class LifecycleEventExecutor {
    public void afterRoute(Object lifecycleHandler, Object page)
            throws TWebInteractionException, TLifecycleEventHandlerException
    {
        if (lifecycleHandler == null) {
            return;
        }
        Class<?> handlerClass = lifecycleHandler.getClass();
        Method afterRouteMethod = getAfterRouteMethod(handlerClass);
        try {
            afterRouteMethod.invoke(lifecycleHandler, page);
        } catch (InvocationTargetException e) {
            throw new TWebInteractionException("Invoking afterRoute method threw exception: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new TLifecycleEventHandlerException("Could not invoke afterRoute method: " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new TLifecycleEventHandlerException("Could not invoke afterRoute method: " + e.getMessage());
        }
    }

    private Method getAfterRouteMethod(Class<?> handlerClass) throws TLifecycleEventHandlerException {
        Method afterRouteMethod = null;
        for (Method method : handlerClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AfterRoute.class)) {
                if (afterRouteMethod != null) {
                    throw new TLifecycleEventHandlerException("More than one @AfterRoute method found");
                }
                afterRouteMethod = method;
            }
        }
        return afterRouteMethod;
    }
}
