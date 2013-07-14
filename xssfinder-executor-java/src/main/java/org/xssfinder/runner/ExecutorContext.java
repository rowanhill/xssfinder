package org.xssfinder.runner;

import org.xssfinder.CrawlStartPoint;
import org.xssfinder.xss.XssGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExecutorContext {
    private final DriverWrapper driverWrapper;
    private final XssGenerator xssGenerator;
    private final Map<String, String> rootPageIdsToUrls = new HashMap<String, String>();

    public ExecutorContext(DriverWrapper driverWrapper, XssGenerator xssGenerator) {
        this.driverWrapper = driverWrapper;
        this.xssGenerator = xssGenerator;
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

    public Map<String, String> putXssAttackStringsInInputs() {
        return driverWrapper.putXssAttackStringsInInputs(xssGenerator);
    }

    public Set<String> getCurrentXssIds() {
        return driverWrapper.getCurrentXssIds();
    }

    public int getFormCount() {
        return driverWrapper.getFormCount();
    }
}
