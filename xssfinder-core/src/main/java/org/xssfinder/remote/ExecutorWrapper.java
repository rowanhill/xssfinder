package org.xssfinder.remote;

import org.apache.thrift.TException;
import org.xssfinder.runner.CommunicationsException;

import java.util.Map;
import java.util.Set;

public class ExecutorWrapper implements Executor.Iface {
    private final Executor.Iface innerExecutor;

    public ExecutorWrapper(Executor.Iface innerExecutor) {
        this.innerExecutor = innerExecutor;
    }

    @Override
    public Set<PageDefinition> getPageDefinitions(String namespaceIdentifier) {
        try {
            return innerExecutor.getPageDefinitions(namespaceIdentifier);
        } catch (TException e) {
            throw new CommunicationsException(e);
        }
    }

    @Override
    public void startRoute(String pageIdentifier) throws TWebInteractionException {
        try {
            innerExecutor.startRoute(pageIdentifier);
        } catch (TException e) {
            throw new CommunicationsException(e);
        }
    }

    @Override
    public Map<String, String> putXssAttackStringsInInputs() throws TWebInteractionException {
        try {
            return innerExecutor.putXssAttackStringsInInputs();
        } catch (TException e) {
            throw new CommunicationsException(e);
        }
    }

    @Override
    public Set<String> getCurrentXssIds() throws TWebInteractionException {
        try {
            return innerExecutor.getCurrentXssIds();
        } catch (TException e) {
            throw new CommunicationsException(e);
        }
    }

    @Override
    public int getFormCount() throws TWebInteractionException {
        try {
            return innerExecutor.getFormCount();
        } catch (TException e) {
            throw new CommunicationsException(e);
        }
    }

    @Override
    public Map<String, String> traverseMethod(MethodDefinition method, TraversalMode mode)
            throws TWebInteractionException
    {
        try {
            return innerExecutor.traverseMethod(method, mode);
        } catch (TException e) {
            throw new CommunicationsException(e);
        } catch (TUntraversableException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void invokeAfterRouteHandler(String rootPageId) throws TWebInteractionException, TLifecycleEventHandlerException {
        try {
            innerExecutor.invokeAfterRouteHandler(rootPageId);
        } catch (TException e) {
            throw new CommunicationsException(e);
        }
    }
}
