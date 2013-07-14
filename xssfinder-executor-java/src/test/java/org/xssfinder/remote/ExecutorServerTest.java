package org.xssfinder.remote;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xssfinder.runner.DefaultHtmlUnitDriverWrapper;
import org.xssfinder.runner.ExecutorContext;
import org.xssfinder.scanner.MethodDefinitionFactory;
import org.xssfinder.scanner.PageDefinitionFactory;
import org.xssfinder.scanner.PageFinder;
import org.xssfinder.xss.XssAttackFactory;
import org.xssfinder.xss.XssGenerator;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ExecutorServerTest {
    private static final int PORT = 9090;
    private ExecutorHandler handler;

    @Before
    public void setUp() {
        handler = new ExecutorHandler(
                new PageFinder(),
                new PageDefinitionFactory(
                        new MethodDefinitionFactory()
                ),
                new ExecutorContext(
                        new DefaultHtmlUnitDriverWrapper(),
                        new XssGenerator(
                                new XssAttackFactory()
                        )
                )
        );
    }

    @Ignore("Creating more than one TSocket in one test run fails")
    @Test(expected = TTransportException.class)
    public void executorClientCannotConnectIfServerIsNotRunning() throws Exception {
        // given
        TTransport transport = new TSocket("localhost", PORT, 5000);
        try {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            Executor.Client client = new Executor.Client(protocol);

            // when
            client.getPageDefinitions("org.dummytest.simple");
        } finally {
            transport.close();
        }
    }

    @Test
    public void executorClientConnectsIfServerIsRunning() throws Exception {
        // given
        startServerAsync();
        Thread.sleep(1000); // TODO Find a better way to ensure server is up and running

        TTransport transport = new TSocket("localhost", PORT, 5000);
        try {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            Executor.Client client = new Executor.Client(protocol);

            // when
            Set<PageDefinition> pageDefinitions = client.getPageDefinitions("org.dummytest.simple");

            // then
            assertThat(pageDefinitions, is(notNullValue()));
        } finally {
            transport.close();
        }
    }

    private void startServerAsync() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ExecutorServer server;
                try {
                    server = new ExecutorServer(PORT, handler);
                    server.serve();
                } catch (TTransportException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
