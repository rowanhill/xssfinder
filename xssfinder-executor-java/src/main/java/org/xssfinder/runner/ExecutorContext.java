package org.xssfinder.runner;

import org.xssfinder.CrawlStartPoint;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.TraversalMode;
import org.xssfinder.scanner.ThriftToReflectionLookup;
import org.xssfinder.xss.XssGenerator;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class ExecutorContext {
    private final DriverWrapper driverWrapper;
    private final XssGenerator xssGenerator;
    private final PageTraverser pageTraverser;
    private final PageInstantiator pageInstantiator;

    private ThriftToReflectionLookup lookup;

    private Object currentPage;

    public ExecutorContext(DriverWrapper driverWrapper, XssGenerator xssGenerator, PageTraverser pageTraverser) {
        this.driverWrapper = driverWrapper;
        this.xssGenerator = xssGenerator;
        this.pageTraverser = pageTraverser;
        this.pageInstantiator = driverWrapper.getPageInstantiator();
    }

    public void setThriftToReflectionLookup(ThriftToReflectionLookup lookup) {
        this.lookup = lookup;
    }

    public void visitUrlOfRootPage(String pageId) {
        Class<?> rootPageClass = lookup.getPageClass(pageId);
        CrawlStartPoint crawlStartPoint = rootPageClass.getAnnotation(CrawlStartPoint.class);
        String url = crawlStartPoint.url();
        driverWrapper.visit(url);

        currentPage = pageInstantiator.instantiatePage(rootPageClass);
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

    public TraversalResult traverseMethod(MethodDefinition methodDefinition, TraversalMode traversalMode) {
        Method method = lookup.getMethod(methodDefinition.getIdentifier());
        return pageTraverser.traverse(currentPage, method, traversalMode);
    }
}
