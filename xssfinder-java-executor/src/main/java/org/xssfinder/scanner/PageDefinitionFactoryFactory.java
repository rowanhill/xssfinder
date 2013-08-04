package org.xssfinder.scanner;

import org.xssfinder.remote.PageDefinition;

import java.util.HashMap;
import java.util.Set;

public class PageDefinitionFactoryFactory {
    public PageDefinitionFactory createPageDefinitionFactory(Set<Class<?>> knownPageClasses, ThriftToReflectionLookup lookup) {
        return new PageDefinitionFactory(
                new MethodDefinitionFactory(),
                knownPageClasses,
                new HashMap<Class<?>, PageDefinition>(),
                lookup
        );
    }
}
