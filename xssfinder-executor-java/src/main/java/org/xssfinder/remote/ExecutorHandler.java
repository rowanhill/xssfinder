package org.xssfinder.remote;

import org.apache.thrift.TException;
import org.xssfinder.runner.ExecutorContext;
import org.xssfinder.runner.TraversalResult;
import org.xssfinder.scanner.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExecutorHandler implements Executor.Iface {
    private final PageFinder pageFinder;
    private final PageDefinitionFactory pageDefinitionFactory;
    private final ThriftToReflectionLookupFactory thriftToReflectionLookupFactory;
    private final ExecutorContext executorContext;

    public ExecutorHandler(
            PageFinder pageFinder,
            PageDefinitionFactory pageDefinitionFactory,
            ThriftToReflectionLookupFactory thriftToReflectionLookupFactory,
            ExecutorContext executorContext
    ) {
        this.pageFinder = pageFinder;
        this.pageDefinitionFactory = pageDefinitionFactory;
        this.thriftToReflectionLookupFactory = thriftToReflectionLookupFactory;
        this.executorContext = executorContext;
    }

    @Override
    public Set<PageDefinition> getPageDefinitions(String namespaceIdentifier) throws TException {
        try {
            Set<Class<?>> pageClasses = pageFinder.findAllPages(namespaceIdentifier);
            Set<PageDefinition> pageDefinitions = new HashSet<PageDefinition>();
            ThriftToReflectionLookup lookup = thriftToReflectionLookupFactory.createLookup();
            for (Class<?> pageClass : pageClasses) {
                PageDefinition pageDefinition = pageDefinitionFactory.createPageDefinition(pageClass, pageClasses, lookup);
                pageDefinitions.add(pageDefinition);
            }
            executorContext.setThriftToReflectionLookup(lookup);
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
    public Map<String, String> traverseMethod(MethodDefinition method, TraversalMode mode)
            throws TException, TUntraversableException, TWebInteractionException
    {
        TraversalResult traversalResult = executorContext.traverseMethod(method, mode);
        return traversalResult.getInputIdsToAttackIds();
    }

    @Override
    public void invokeAfterRouteHandler(String rootPageIdentifier)
            throws TException, TWebInteractionException, TLifecycleEventHandlerException
    {
        executorContext.invokeAfterRouteHandler(rootPageIdentifier);
    }
}
