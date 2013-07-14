package org.xssfinder.runner;

import org.xssfinder.CrawlStartPoint;
import org.xssfinder.remote.TraversalMode;
import org.xssfinder.xss.XssGenerator;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExecutorContext {
    private final DriverWrapper driverWrapper;
    private final XssGenerator xssGenerator;
    private final PageTraverser pageTraverser;
    private final PageInstantiator pageInstantiator;

    private final Map<String, String> rootPageIdsToUrls = new HashMap<String, String>();
    private final Map<String, Class<?>> pageIdsToClasses = new HashMap<String, Class<?>>();

    private Object currentPage;

    public ExecutorContext(DriverWrapper driverWrapper, XssGenerator xssGenerator, PageTraverser pageTraverser) {
        this.driverWrapper = driverWrapper;
        this.xssGenerator = xssGenerator;
        this.pageTraverser = pageTraverser;
        this.pageInstantiator = driverWrapper.getPageInstantiator();
    }

    public void addPageMapping(String pageId, Class<?> pageClass) {
        if (pageClass.isAnnotationPresent(CrawlStartPoint.class)) {
            CrawlStartPoint crawlStartPoint = pageClass.getAnnotation(CrawlStartPoint.class);
            String url = crawlStartPoint.url();
            rootPageIdsToUrls.put(pageId, url);
        }
        pageIdsToClasses.put(pageId, pageClass);
    }

    public void visitUrlOfRootPage(String pageId) {
        String url = rootPageIdsToUrls.get(pageId);
        driverWrapper.visit(url);

        Class<?> pageClass = pageIdsToClasses.get(pageId);
        currentPage = pageInstantiator.instantiatePage(pageClass);
    }

    public Map<String, String> putXssAttackStringsInInputs() {
        return driverWrapper.putXssAttackStringsInInputs(xssGenerator);
    }

    public Set<String> getCurrentXssIds() {
        return driverWrapper.getCurrentXssIds();
    }

    public int getFormCount() {
        return driverWrapper.getFormCount();
    }

    public TraversalResult traverseMethod(Method method, TraversalMode traversalMode) {
        return pageTraverser.traverse(currentPage, method, traversalMode);
    }
}
