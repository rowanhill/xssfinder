package org.xssfinder.scanner;

import org.xssfinder.CrawlStartPoint;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PageDefinitionFactory {
    private final MethodDefinitionFactory methodDefinitionFactory;
    private final Map<Class<?>, PageDefinition> pageDefinitionCache;

    public PageDefinitionFactory(MethodDefinitionFactory methodDefinitionFactory) {
        this.methodDefinitionFactory = methodDefinitionFactory;
        this.pageDefinitionCache = new HashMap<Class<?>, PageDefinition>();
    }

    public PageDefinition createPageDefinition(Class<?> pageClass, Set<Class<?>> knownPageClasses) {
        if (!pageDefinitionCache.containsKey(pageClass)) {
            String identifier = pageClass.getCanonicalName();
            Set<MethodDefinition> methods = getMethodDefinitions(pageClass, knownPageClasses);
            boolean isCrawlStartPoint = pageClass.isAnnotationPresent(CrawlStartPoint.class);
            PageDefinition pageDefinition = new PageDefinition(identifier, methods, isCrawlStartPoint);
            if (isCrawlStartPoint) {
                CrawlStartPoint crawlStartPoint = pageClass.getAnnotation(CrawlStartPoint.class);
                pageDefinition.setStartPointUrl(crawlStartPoint.url());
            }
            pageDefinitionCache.put(pageClass, pageDefinition);
        }
        return pageDefinitionCache.get(pageClass);
    }

    private Set<MethodDefinition> getMethodDefinitions(Class<?> pageClass, Set<Class<?>> knownPageClasses) {
        Set<MethodDefinition> methods = new HashSet<MethodDefinition>();
        for (Method method : pageClass.getDeclaredMethods()) {
            if (knownPageClasses.contains(method.getReturnType())) {
                MethodDefinition methodDefinition = methodDefinitionFactory.createMethodDefinition(
                        method,
                        pageDefinitionCache,
                        this,
                        knownPageClasses
                );
                methods.add(methodDefinition);
            }
        }
        return methods;
    }
}
