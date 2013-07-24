package org.xssfinder.remote;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

public class ExecutorServer {

    private final TServer server;

    public ExecutorServer(int port, ExecutorHandler handler) throws TTransportException {
        Executor.Processor<ExecutorHandler> processor = new Executor.Processor<ExecutorHandler>(handler);
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
