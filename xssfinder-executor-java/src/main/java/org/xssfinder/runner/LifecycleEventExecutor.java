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
            String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new TWebInteractionException("Invoking afterRoute method threw exception: " + message);
        } catch (IllegalArgumentException e) {
            String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new TLifecycleEventHandlerException("Could not invoke afterRoute method: " + message);
        } catch (IllegalAccessException e) {
            String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new TLifecycleEventHandlerException("Could not invoke afterRoute method: " + message);
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
