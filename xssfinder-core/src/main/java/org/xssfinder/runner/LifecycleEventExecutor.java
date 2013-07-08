package org.xssfinder.runner;

import org.xssfinder.AfterRoute;

import java.lang.reflect.Method;

/**
 * Executes route lifecycle event handlers as appropriate on lifecycle handlers
 */
class LifecycleEventExecutor {
    public void afterRoute(Object lifecycleHandler, Object page) {
        if (lifecycleHandler == null) {
            return;
        }
        Class<?> handlerClass = lifecycleHandler.getClass();
        Method afterRouteMethod = getAfterRouteMethod(handlerClass);
        try {
            afterRouteMethod.invoke(lifecycleHandler, page);
        } catch (Exception e) {
            throw new LifecycleEventException(e);
        }
    }

    private Method getAfterRouteMethod(Class<?> handlerClass) {
        Method afterRouteMethod = null;
        for (Method method : handlerClass.getMethods()) {
            if (method.isAnnotationPresent(AfterRoute.class)) {
                if (afterRouteMethod != null) {
                    throw new LifecycleEventException("More than one @AfterRoute method found");
                }
                afterRouteMethod = method;
            }
        }
        return afterRouteMethod;
    }
}
