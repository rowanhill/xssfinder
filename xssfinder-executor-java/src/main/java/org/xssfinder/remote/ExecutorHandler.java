package org.xssfinder.remote;

import org.apache.thrift.TException;
import org.xssfinder.scanner.NoPagesFoundException;
import org.xssfinder.scanner.PageDefinitionFactory;
import org.xssfinder.scanner.PageFinder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExecutorHandler implements Executor.Iface {
    private final PageFinder pageFinder;
    private final PageDefinitionFactory pageDefinitionFactory;

    public ExecutorHandler(PageFinder pageFinder, PageDefinitionFactory pageDefinitionFactory) {
        this.pageFinder = pageFinder;
        this.pageDefinitionFactory = pageDefinitionFactory;
    }

    @Override
    public Set<PageDefinition> getPageDefinitions(String namespaceIdentifier) throws TException {
        try {
            Set<Class<?>> pageClasses = pageFinder.findAllPages(namespaceIdentifier);
            Set<PageDefinition> pageDefinitions = new HashSet<PageDefinition>();
            for (Class<?> pageClass : pageClasses) {
                pageDefinitions.add(pageDefinitionFactory.createPageDefinition(pageClass, pageClasses));
            }
            return pageDefinitions;
        } catch (NoPagesFoundException e) {
            throw new TException(e);
        }
    }

    @Override
    public void visit(String url) throws TException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, String> putXssAttackStringsInInputs() throws TException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<String> getCurrentXssIds() throws TException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getFormCount() throws TException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, String> traverseMethod(MethodDefinition method, TraversalMode mode) throws TException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void invokeAfterRouteHandler() throws TException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
