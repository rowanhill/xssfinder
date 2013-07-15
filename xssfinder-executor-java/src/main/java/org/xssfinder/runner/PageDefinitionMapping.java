package org.xssfinder.runner;

import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.lang.reflect.Method;
import java.util.Map;

public class PageDefinitionMapping {
    private final Class<?> pageClass;
    private final PageDefinition pageDefinition;
    private final Map<MethodDefinition, Method> methodMap;

    public PageDefinitionMapping(Class<?> pageClass, PageDefinition pageDefinition, Map<MethodDefinition, Method> methodMap) {
        this.pageClass = pageClass;
        this.pageDefinition = pageDefinition;
        this.methodMap = methodMap;
    }

    public Class<?> getPageClass() {
        return pageClass;
    }

    public PageDefinition getPageDefinition() {
        return pageDefinition;
    }

    public Map<MethodDefinition, Method> getMethodMapping() {
        return methodMap;
    }
}
