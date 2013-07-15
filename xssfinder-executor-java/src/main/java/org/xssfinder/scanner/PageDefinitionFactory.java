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
            Map<MethodDefinition, Method> methodMapping = getMethodDefinitions(pageClass, knownPageClasses);
            boolean isCrawlStartPoint = pageClass.isAnnotationPresent(CrawlStartPoint.class);
            PageDefinition pageDefinition = new PageDefinition(identifier, methodMapping.keySet(), isCrawlStartPoint);
            PageDefinitionMapping mapping = new PageDefinitionMapping(
                    pageClass,
                    pageDefinition,
                    methodMapping
            );
            pageDefinitionCache.put(pageClass, mapping);
        }
        return pageDefinitionCache.get(pageClass);
    }

    private Map<MethodDefinition, Method> getMethodDefinitions(Class<?> pageClass, Set<Class<?>> knownPageClasses) {
        Map<MethodDefinition, Method> methods = new HashMap<MethodDefinition, Method>();
        for (Method method : pageClass.getDeclaredMethods()) {
            if (knownPageClasses.contains(method.getReturnType())) {
                MethodDefinition methodDefinition = methodDefinitionFactory.createMethodDefinition(
                        method,
                        pageDefinitionCache,
                        this,
                        knownPageClasses
                );
                methods.put(methodDefinition, method);
            }
        }
        return methods;
    }
}
