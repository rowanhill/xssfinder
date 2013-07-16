package org.xssfinder.scanner;

import org.xssfinder.CrawlStartPoint;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;
import org.xssfinder.runner.PageDefinitionMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PageDefinitionFactory {
    private final MethodDefinitionFactory methodDefinitionFactory;
    private final Map<Class<?>, PageDefinitionMapping> pageDefinitionCache;

    public PageDefinitionFactory(MethodDefinitionFactory methodDefinitionFactory) {
        this.methodDefinitionFactory = methodDefinitionFactory;
        this.pageDefinitionCache = new HashMap<Class<?>, PageDefinitionMapping>();
    }

    public PageDefinitionMapping createPageDefinition(Class<?> pageClass, Set<Class<?>> knownPageClasses) {
        if (!pageDefinitionCache.containsKey(pageClass)) {
            String identifier = pageClass.getCanonicalName();
            boolean isCrawlStartPoint = pageClass.isAnnotationPresent(CrawlStartPoint.class);
            Map<MethodDefinition, Method> methodMapping = new HashMap<MethodDefinition, Method>();
            PageDefinition pageDefinition = new PageDefinition(identifier, methodMapping.keySet(), isCrawlStartPoint);
            PageDefinitionMapping mapping = new PageDefinitionMapping(
                    pageClass,
                    pageDefinition,
                    methodMapping
            );
            pageDefinitionCache.put(pageClass, mapping);

            // Add the methods after creating the page & putting the mapping in the cache, in case any of the methods
            // are circular (i.e. return the type they're defined on).
            methodMapping.putAll(getMethodDefinitions(pageClass, knownPageClasses));
        }
        return pageDefinitionCache.get(pageClass);
    }

    private Map<MethodDefinition, Method> getMethodDefinitions(Class<?> pageClass, Set<Class<?>> knownPageClasses) {
        Map<MethodDefinition, Method> methods = new HashMap<MethodDefinition, Method>();
        for (Method method : pageClass.getDeclaredMethods()) {
            if (knownPageClasses.contains(method.getReturnType())) {
                MethodDefinition methodDefinition = methodDefinitionFactory.createMethodDefinition(
                        method,
                        //TODO This "new" is purely for testing; there must be a way to achieve the same thing without it
                        new HashMap<Class<?>, PageDefinitionMapping>(pageDefinitionCache),
                        this,
                        knownPageClasses
                );
                methods.put(methodDefinition, method);
            }
        }
        return methods;
    }
}
