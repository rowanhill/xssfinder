package org.xssfinder.scanner;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ThriftToReflectionLookupFactoryTest {
    @Test
    public void createsAThriftToReflectionLookupWithoutException() {
        // given
        ThriftToReflectionLookupFactory factory = new ThriftToReflectionLookupFactory();

        // when
        ThriftToReflectionLookup lookup = factory.createLookup();

        // then
        assertThat(lookup, is(notNullValue()));
    }
}
