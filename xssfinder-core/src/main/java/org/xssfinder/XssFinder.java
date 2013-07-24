package org.xssfinder;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.xssfinder.remote.Executor;
import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.remote.PageDefinition;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.routing.Route;
import org.xssfinder.routing.RouteGenerator;
import org.xssfinder.routing.RouteGeneratorFactory;
import org.xssfinder.runner.RouteRunner;
import org.xssfinder.runner.RouteRunnerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class XssFinder {
    private final String host;
    private final int port;
    private final int timeoutMillis;
    private final RouteRunnerFactory routeRunnerFactory;
    private final RouteGeneratorFactory routeGeneratorFactory;

    public XssFinder(String host, int port, int timeoutMillis) {
        this.host = host;
        this.port = port;
        this.timeoutMillis = timeoutMillis;

        routeRunnerFactory = new RouteRunnerFactory();
        routeGeneratorFactory = new RouteGeneratorFactory();
    }

    public XssJournal findXssVulnerabilities(String packageId, String reportFile) throws TException, IOException {
        TTransport transport = new TSocket(host, port, timeoutMillis);
        try {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            Executor.Client client = new Executor.Client(protocol);
            ExecutorWrapper executorWrapper = new ExecutorWrapper(client);

            RouteRunner runner = routeRunnerFactory.createRouteRunner(executorWrapper, reportFile);

            Set<PageDefinition> pageDefinitions = client.getPageDefinitions(packageId);
            RouteGenerator routeGenerator = routeGeneratorFactory.createRouteGenerator();
            List<Route> routes = routeGenerator.generateRoutes(pageDefinitions);

            return runner.run(routes);
        } finally {
            transport.close();
        }
    }
}
