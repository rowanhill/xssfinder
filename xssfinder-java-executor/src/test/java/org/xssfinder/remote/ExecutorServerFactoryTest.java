package org.xssfinder.remote;

import org.junit.Test;
import org.xssfinder.remote.ExecutorServer;
import org.xssfinder.remote.ExecutorServerFactory;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ExecutorServerFactoryTest {
    @Test
    public void createsExecutorServer() throws Exception {
        // given
        ExecutorServerFactory factory = new ExecutorServerFactory();

        // when
        ExecutorServer server = factory.createExecutorServer(9091);

        // then
        assertThat(server, is(notNullValue()));
    }
}
