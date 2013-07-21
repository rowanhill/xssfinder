package org.xssfinder.runner;

import org.xssfinder.CrawlStartPoint;
import org.xssfinder.reflection.*;
import org.xssfinder.reflection.InstantiationException;
import org.xssfinder.remote.*;
import org.xssfinder.scanner.ThriftToReflectionLookup;
import org.xssfinder.xss.XssGenerator;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class ExecutorContext {
    private final DriverWrapper driverWrapper;
    private final XssGenerator xssGenerator;
    private final PageTraverser pageTraverser;
    private final Instantiator instantiator;
    private final LifecycleEventExecutor lifecycleEventExecutor;
    private final PageInstantiator pageInstantiator;

    private ThriftToReflectionLookup lookup;

    private Object currentPage;

    public ExecutorContext(
            DriverWrapper driverWrapper,
            XssGenerator xssGenerator,
            PageTraverser pageTraverser,
            Instantiator instantiator,
            LifecycleEventExecutor lifecycleEventExecutor
    ) {
        this.driverWrapper = driverWrapper;
        this.xssGenerator = xssGenerator;
        this.pageTraverser = pageTraverser;
        this.instantiator = instantiator;
        this.lifecycleEventExecutor = lifecycleEventExecutor;
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

    public TraversalResult traverseMethod(MethodDefinition methodDefinition, TraversalMode traversalMode)
            throws TUntraversableException, TWebInteractionException
    {
        Method method = lookup.getMethod(methodDefinition.getIdentifier());
        TraversalResult traversalResult = pageTraverser.traverse(currentPage, method, traversalMode);
        currentPage = traversalResult.getPage();
        return traversalResult;
    }

    public void invokeAfterRouteHandler(String rootPageId)
            throws TWebInteractionException, TLifecycleEventHandlerException
    {
        Class<?> rootClass = lookup.getPageClass(rootPageId);
        Object lifecycleHandler = createLifecycleEventHandler(rootClass);
        if (lifecycleHandler != null) {
            lifecycleEventExecutor.afterRoute(lifecycleHandler, currentPage);
        }
    }

    private Object createLifecycleEventHandler(Class<?> rootClass) throws TLifecycleEventHandlerException {
        CrawlStartPoint crawlStartPoint = rootClass.getAnnotation(CrawlStartPoint.class);
        Class<?> handlerClass = crawlStartPoint.lifecycleHandler();
        if (handlerClass == Object.class) {
            // Object is the default lifecycle handler; it indicates that no handler has been set, so we return null
            return null;
        }
        try {
            return instantiator.instantiate(handlerClass);
        } catch (InstantiationException ex) {
            throw new TLifecycleEventHandlerException("Could not instantiate " + handlerClass.getSimpleName() + " : "
                    + ex.getMessage());
        }
    }
}
