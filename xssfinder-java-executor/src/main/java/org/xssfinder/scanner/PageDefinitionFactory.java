package org.xssfinder.scanner;

import org.xssfinder.CrawlStartPoint;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PageDefinitionFactory {
    private final MethodDefinitionFactory methodDefinitionFactory;
    private final Set<Class<?>> knownPageClasses;
    private final Map<Class<?>, PageDefinition> pageDefinitionCache;
    private final ThriftToReflectionLookup lookup;

    public PageDefinitionFactory(
            MethodDefinitionFactory methodDefinitionFactory,
            Set<Class<?>> knownPageClasses,
            Map<Class<?>, PageDefinition> pageDefinitionCache,
            ThriftToReflectionLookup lookup
    ) {
        this.methodDefinitionFactory = methodDefinitionFactory;
        this.knownPageClasses = knownPageClasses;
        this.pageDefinitionCache = pageDefinitionCache;
        this.lookup = lookup;
    }

    public PageDefinition createPageDefinition(Class<?> pageClass) {
        if (!pageDefinitionCache.containsKey(pageClass)) {
            String identifier = pageClass.getCanonicalName();
            boolean isCrawlStartPoint = pageClass.isAnnotationPresent(CrawlStartPoint.class);
            Map<MethodDefinition, Method> methodMapping = new HashMap<MethodDefinition, Method>();
            PageDefinition pageDefinition = new PageDefinition(identifier, methodMapping.keySet(), isCrawlStartPoint);
            pageDefinitionCache.put(pageClass, pageDefinition);
            lookup.putPageClass(pageDefinition.getIdentifier(), pageClass);

            // Add the methods after creating the page & putting the mapping in the cache, in case any of the methods
            // are circular (i.e. return the type they're defined on).
            methodMapping.putAll(getMethodDefinitions(pageClass, knownPageClasses));

            for (Map.Entry<MethodDefinition, Method> entry : methodMapping.entrySet()) {
                lookup.putMethod(entry.getKey().getIdentifier(), entry.getValue());
            }
        }
        return pageDefinitionCache.get(pageClass);
    }

    private Map<MethodDefinition, Method> getMethodDefinitions(Class<?> pageClass, Set<Class<?>> knownPageClasses) {
        Map<MethodDefinition, Method> methods = new HashMap<MethodDefinition, Method>();
        for (Method method : pageClass.getDeclaredMethods()) {
            if (knownPageClasses.contains(method.getReturnType())) {
                MethodDefinition methodDefinition = methodDefinitionFactory.createMethodDefinition(method);
                methods.put(methodDefinition, method);
            }
        }
        return methods;
    }
}
