package org.xssfinder.runner;

import org.xssfinder.CrawlStartPoint;

import java.util.HashMap;
import java.util.Map;

public class ExecutorContext {
    private final DriverWrapper driverWrapper;
    private final Map<String, String> rootPageIdsToUrls = new HashMap<String, String>();

    public ExecutorContext(DriverWrapper driverWrapper) {
        this.driverWrapper = driverWrapper;
    }

    public void addPageMapping(String pageId, Class<?> pageClass) {
        if (pageClass.isAnnotationPresent(CrawlStartPoint.class)) {
            CrawlStartPoint crawlStartPoint = pageClass.getAnnotation(CrawlStartPoint.class);
            String url = crawlStartPoint.url();
            rootPageIdsToUrls.put(pageId, url);
        }
    }

    public void visitUrlOfRootPage(String pageId) {
        String url = rootPageIdsToUrls.get(pageId);
        driverWrapper.visit(url);
    }
}
