package org.xssfinder.remote;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TMemoryBuffer;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class RunnerServerTest {
    private static final int PORT = 9090;

    @Ignore("Creating more than one TSocket in one test run fails")
    @Test(expected = TTransportException.class)
    public void runnerClientCannotConnectIfServerIsNotRunning() throws Exception {
        // given
        TTransport transport = new TSocket("localhost", PORT, 5000);
        try {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            Runner.Client client = new Runner.Client(protocol);

            // when
            client.getPageDefinitions("org.dummytest.simple");
        } finally {
            transport.close();
        }
    }

    @Test
    public void runnerClientConnectsIfServerIsRunning() throws Exception {
        // given
        startServerAsync();
        TTransport transport = new TSocket("localhost", PORT, 5000);
        try {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            Runner.Client client = new Runner.Client(protocol);

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
                RunnerServer server;
                try {
                    server = new RunnerServer(PORT);
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
