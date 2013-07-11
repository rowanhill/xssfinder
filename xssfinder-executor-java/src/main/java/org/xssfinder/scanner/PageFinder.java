package org.xssfinder.scanner;

import org.reflections.Reflections;
import org.xssfinder.Page;

import java.util.Set;

/**
 * Finds all @Page annotated classes on the classpath within a package
 */
public class PageFinder {
    public Set<Class<?>> findAllPages(String packageName) throws NoPagesFoundException {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Page.class);
        if (annotatedClasses.isEmpty()) {
            throw new NoPagesFoundException();
        }
        return annotatedClasses;
    }
}
