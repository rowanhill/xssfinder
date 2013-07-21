package org.xssfinder.scanner;

import org.apache.thrift.transport.TTransportException;
import org.xssfinder.reflection.Instantiator;
import org.xssfinder.remote.ExecutorHandler;
import org.xssfinder.remote.ExecutorServer;
import org.xssfinder.runner.*;
import org.xssfinder.xss.XssAttackFactory;
import org.xssfinder.xss.XssGenerator;

public class ExecutorServerFactory {
    public ExecutorServer createExecutorServer(int port) throws TTransportException {
        Instantiator instantiator = new Instantiator();
        ExecutorHandler executorHandler = new ExecutorHandler(
                new PageFinder(),
                new PageDefinitionFactoryFactory(),
                new ThriftToReflectionLookupFactory(),
                new ExecutorContext(
                        new DefaultHtmlUnitDriverWrapper(),
                        new XssGenerator(new XssAttackFactory()),
                        new PageTraverser(
                                new CustomTraverserInstantiator(instantiator),
                                new CustomSubmitterInstantiator(instantiator),
                                new LabelledXssGeneratorFactory()
                        ),
                        instantiator,
                        new LifecycleEventExecutor()
                )
        );
        return new ExecutorServer(port, executorHandler);
    }
}
