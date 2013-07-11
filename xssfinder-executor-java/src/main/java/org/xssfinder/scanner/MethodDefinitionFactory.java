package org.xssfinder.scanner;

import org.xssfinder.SubmitAction;
import org.xssfinder.TraverseWith;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class MethodDefinitionFactory {
    public MethodDefinition createMethodDefinition(
            Method method,
            Map<Class<?>, PageDefinition> pageDefinitionCache,
            PageDefinitionFactory pageDefinitionFactory,
            Set<Class<?>> knownPageClasses
    ) {
        String identifier = method.getName();
        Class<?> returnTypeClass = method.getReturnType();
        PageDefinition returnType = getPageDefinition(
                pageDefinitionCache, pageDefinitionFactory, knownPageClasses, returnTypeClass);
        boolean hasArgs = method.getParameterTypes().length > 0;
        boolean isSubmit = method.isAnnotationPresent(SubmitAction.class);
        boolean hasCustomTraverser = method.isAnnotationPresent(TraverseWith.class);
        return new MethodDefinition(
                identifier,
                returnType,
                hasArgs,
                isSubmit,
                hasCustomTraverser
        );
    }

    private PageDefinition getPageDefinition(
            Map<Class<?>, PageDefinition> pageDefinitionCache,
            PageDefinitionFactory pageDefinitionFactory,
            Set<Class<?>> knownPageClasses,
            Class<?> returnTypeClass
    ) {
        PageDefinition returnType = pageDefinitionCache.get(returnTypeClass);
        if (returnType == null) {
            returnType = pageDefinitionFactory.createPageDefinition(returnTypeClass, knownPageClasses);
        }
        return returnType;
    }
}
