package org.xssfinder.remote;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.xssfinder.scanner.MethodDefinitionFactory;
import org.xssfinder.scanner.PageDefinitionFactory;
import org.xssfinder.scanner.PageFinder;

public class RunnerServer {

    private final TServer server;

    public RunnerServer(int port) throws TTransportException {
        RunnerHandler handler = new RunnerHandler(
                new PageFinder(),
                new PageDefinitionFactory(new MethodDefinitionFactory())
        );
        Runner.Processor<RunnerHandler> processor = new Runner.Processor<RunnerHandler>(handler);
        TServerTransport transport = new TServerSocket(port);
        server = new TSimpleServer(new TServer.Args(transport).processor(processor));
    }

    public void serve() {
        server.serve();
    }

    public void stop() {
        server.stop();
    }
}
