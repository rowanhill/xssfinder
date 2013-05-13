package org.xssfinder.runner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RouteRunnerFactoryTest {
    @Mock
    private DriverWrapper mockDriverWrapper;

    @Test
    public void constructsRunner() {
        // given
        RouteRunnerFactory factory = new RouteRunnerFactory();

        // when
        RouteRunner runner = factory.createRouteRunner(mockDriverWrapper, "somefile.html");

        // then
        assertThat(runner, is(notNullValue()));
    }
}
