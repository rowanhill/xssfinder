package org.xssfinder.scanner;

import org.reflections.Reflections;
import org.xssfinder.Page;

import java.util.Set;

public class PageFinder {
    private final String packageName;

    public PageFinder(String packageName) {
        this.packageName = packageName;
    }

    public Set<Class<?>> findAllPages() {
        Reflections reflections = new Reflections(this.packageName);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Page.class);
        if (annotatedClasses.isEmpty()) {
            throw new NoPagesFoundException();
        }
        return annotatedClasses;
    }
}
