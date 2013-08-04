package org.xssfinder.scanner;

import org.xssfinder.SubmitAction;
import org.xssfinder.TraverseWith;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.lang.reflect.Method;

public class MethodDefinitionFactory {
    public MethodDefinition createMethodDefinition(
            Method method,
            PageDefinitionFactory pageDefinitionFactory
    ) {
        String identifier = method.getName();
        Class<?> returnTypeClass = method.getReturnType();
        PageDefinition returnType = getPageDefinition(pageDefinitionFactory, returnTypeClass);
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
            Class<?> returnTypeClass
    ) {
        return pageDefinitionFactory.createPageDefinition(returnTypeClass);
    }
}
