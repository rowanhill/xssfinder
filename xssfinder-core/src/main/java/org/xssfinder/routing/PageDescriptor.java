package org.xssfinder.routing;

import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;
import org.xssfinder.SubmitAction;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Description of a page and its corresponding page object
 */
public class PageDescriptor {
    private final boolean isRoot;
    private final Set<Method> traversalMethods;
    private final Set<Method> submitMethods;
    private final Class<?> pageClass;

    /**
     * @param pageClass The class of the page object representing this page
     */
    public PageDescriptor(Class<?> pageClass) {
        isRoot = pageClass.isAnnotationPresent(CrawlStartPoint.class);
        traversalMethods = findTraversalMethods(pageClass);
        submitMethods = findSubmitMethods(traversalMethods);
        this.pageClass = pageClass;
    }

    private Set<Method> findTraversalMethods(Class<?> pageClass) {
        Set<Method> traversalMethods = new HashSet<Method>();
        for (Method method : pageClass.getMethods()) {
            Class<?> returnType = method.getReturnType();
            if (returnType != this.getClass() && returnType.isAnnotationPresent(Page.class)) {
                traversalMethods.add(method);
            }
        }
        return traversalMethods;
    }

    /**
     * @param traversalMethods A set of all methods on the page object that traverse to another page
     * @return The subset of traversalMethods which are @SubmitActions
     */
    Set<Method> findSubmitMethods(Set<Method> traversalMethods) {
        Set<Method> submitMethods = new HashSet<Method>();
        for (Method traversalMethod : traversalMethods) {
            if (traversalMethod.isAnnotationPresent(SubmitAction.class)) {
                submitMethods.add(traversalMethod);
            }
        }
        return submitMethods;
    }

    /**
     * @return True if this page is the root of a route
     */
    public boolean isRoot() {
        return isRoot;
    }

    /**
     * @return A set of methods which traverse from one page to another
     */
    public Set<Method> getTraversalMethods() {
        return traversalMethods;
    }

    /**
     * @return A set of traversal methods which submit the page
     */
    public Set<Method> getSubmitMethods() {
        return submitMethods;
    }

    /**
     * @return The page object described by this object
     */
    public Class<?> getPageClass() {
        return pageClass;
    }

    public String getCrawlStartPointUrl() {
        CrawlStartPoint annotation = getPageClass().getAnnotation(CrawlStartPoint.class);
        if (annotation == null) {
            throw new NotAStartPointException();
        }
        return annotation.url();
    }
}
