package org.xssfinder.scanner;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ThriftToReflectionLookup {
    private final Map<String, Class<?>> pageClassLookup;
    private final Map<String, Method> methodLookup;

    public ThriftToReflectionLookup() {
        pageClassLookup = new HashMap<String, Class<?>>();
        methodLookup = new HashMap<String, Method>();
    }

    public Class<?> getPageClass(String pageId) {
        return pageClassLookup.get(pageId);
    }

    public Method getMethod(String methodId) {
        return methodLookup.get(methodId);
    }

    public void putPageClass(String pageId, Class<?> pageClass) {
        pageClassLookup.put(pageId, pageClass);
    }

    public void putMethod(String methodId, Method method) {
        methodLookup.put(methodId, method);
    }
}
