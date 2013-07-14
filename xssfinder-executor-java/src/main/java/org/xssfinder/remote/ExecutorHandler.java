package org.xssfinder.remote;

import org.apache.thrift.TException;
import org.xssfinder.runner.DriverWrapper;
import org.xssfinder.scanner.NoPagesFoundException;
import org.xssfinder.scanner.PageDefinitionFactory;
import org.xssfinder.scanner.PageFinder;
import org.xssfinder.xss.XssGenerator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExecutorHandler implements Executor.Iface {
    private final PageFinder pageFinder;
    private final PageDefinitionFactory pageDefinitionFactory;
    private final DriverWrapper driverWrapper;
    private final XssGenerator xssGenerator;

    public ExecutorHandler(
            PageFinder pageFinder,
            PageDefinitionFactory pageDefinitionFactory,
            DriverWrapper driverWrapper,
            XssGenerator xssGenerator
    ) {
        this.pageFinder = pageFinder;
        this.pageDefinitionFactory = pageDefinitionFactory;
        this.driverWrapper = driverWrapper;
        this.xssGenerator = xssGenerator;
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
        driverWrapper.visit(url);
    }

    @Override
    public Map<String, String> putXssAttackStringsInInputs() throws TException {
        return driverWrapper.putXssAttackStringsInInputs(xssGenerator);
    }

    @Override
    public Set<String> getCurrentXssIds() throws TException {
        return driverWrapper.getCurrentXssIds();
    }

    @Override
    public int getFormCount() throws TException {
        return driverWrapper.getFormCount();
    }

    @Override
    public Map<String, String> traverseMethod(MethodDefinition method, TraversalMode mode) throws TException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
