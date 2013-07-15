package org.xssfinder.remote;

import org.apache.thrift.TException;
import org.xssfinder.runner.ExecutorContext;
import org.xssfinder.runner.PageDefinitionMapping;
import org.xssfinder.runner.TraversalResult;
import org.xssfinder.scanner.NoPagesFoundException;
import org.xssfinder.scanner.PageDefinitionFactory;
import org.xssfinder.scanner.PageFinder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExecutorHandler implements Executor.Iface {
    private final PageFinder pageFinder;
    private final PageDefinitionFactory pageDefinitionFactory;
    private final ExecutorContext executorContext;

    public ExecutorHandler(
            PageFinder pageFinder,
            PageDefinitionFactory pageDefinitionFactory,
            ExecutorContext executorContext) {
        this.pageFinder = pageFinder;
        this.pageDefinitionFactory = pageDefinitionFactory;
        this.executorContext = executorContext;
    }

    @Override
    public Set<PageDefinition> getPageDefinitions(String namespaceIdentifier) throws TException {
        try {
            Set<Class<?>> pageClasses = pageFinder.findAllPages(namespaceIdentifier);
            Set<PageDefinition> pageDefinitions = new HashSet<PageDefinition>();
            for (Class<?> pageClass : pageClasses) {
                PageDefinitionMapping pageDefinitionMapping = pageDefinitionFactory.createPageDefinition(pageClass, pageClasses);
                pageDefinitions.add(pageDefinitionMapping.getPageDefinition());
                executorContext.addPageMapping(pageDefinitionMapping);
            }
            return pageDefinitions;
        } catch (NoPagesFoundException e) {
            throw new TException(e);
        }
    }

    @Override
    public void startRoute(String pageIdentifier) throws TException {
        executorContext.visitUrlOfRootPage(pageIdentifier);
    }

    @Override
    public Map<String, String> putXssAttackStringsInInputs() throws TException {
        return executorContext.putXssAttackStringsInInputs();
    }

    @Override
    public Set<String> getCurrentXssIds() throws TException {
        return executorContext.getCurrentXssIds();
    }

    @Override
    public int getFormCount() throws TException {
        return executorContext.getFormCount();
    }

    @Override
    public Map<String, String> traverseMethod(MethodDefinition method, TraversalMode mode) throws TException {
        TraversalResult traversalResult = executorContext.traverseMethod(method, mode);
        return traversalResult.getInputIdsToAttackIds();
    }

    @Override
    public void invokeAfterRouteHandler() throws TException {
        //To change body of implemented methods use File | Settings | File Templates.
        /*
        PageDefinition pageClass = getRootPageClass();
        Class<?> handlerClass = startPointAnnotation.lifecycleHandler();
        if (handlerClass == Object.class) {
            // Object is the default lifecycle handler; it indicates that no handler has been set, so we return null
            return null;
        }
        try {
            return instantiator.instantiate(handlerClass);
        } catch (InstantiationException ex) {
            throw new LifecycleEventException(ex);
        }
        */
    }
}
