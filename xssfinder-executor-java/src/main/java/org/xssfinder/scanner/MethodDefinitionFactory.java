package org.xssfinder.scanner;

import org.xssfinder.SubmitAction;
import org.xssfinder.TraverseWith;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.lang.reflect.Method;
import java.util.Set;

public class MethodDefinitionFactory {
    public MethodDefinition createMethodDefinition(
            Method method,
            PageDefinitionFactory pageDefinitionFactory,
            Set<Class<?>> knownPageClasses,
            ThriftToReflectionLookup lookup
    ) {
        String identifier = method.getName();
        Class<?> returnTypeClass = method.getReturnType();
        PageDefinition returnType = getPageDefinition(
                pageDefinitionFactory, knownPageClasses, returnTypeClass, lookup);
        Class<?> owningTypeClass = method.getDeclaringClass();
        boolean hasArgs = method.getParameterTypes().length > 0;
        boolean isSubmit = method.isAnnotationPresent(SubmitAction.class);
        boolean hasCustomTraverser = method.isAnnotationPresent(TraverseWith.class);
        return new MethodDefinition(
                identifier,
                returnType.getIdentifier(),
                owningTypeClass.getCanonicalName(),
                hasArgs,
                isSubmit,
                hasCustomTraverser
        );
    }

    private PageDefinition getPageDefinition(
            PageDefinitionFactory pageDefinitionFactory,
            Set<Class<?>> knownPageClasses,
            Class<?> returnTypeClass,
            ThriftToReflectionLookup lookup
    ) {
        return pageDefinitionFactory.createPageDefinition(returnTypeClass, knownPageClasses, lookup);
    }
}
