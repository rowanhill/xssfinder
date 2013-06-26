package org.xssfinder.routing;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class RouteGeneratorFactoryTest {
    @Test
    public void createsRouteGenerator() {
        // given
        RouteGeneratorFactory factory = new RouteGeneratorFactory();

        // when
        RouteGenerator generator = factory.createRouteGenerator();

        // then
        assertThat(generator, is(notNullValue()));
    }
}
