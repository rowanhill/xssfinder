package org.xssfinder.scanner;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class PageDefinitionFactoryFactoryTest {
    @Test
    public void createsPageDefinitionFactory() {
        // given
        PageDefinitionFactoryFactory factoryFactory = new PageDefinitionFactoryFactory();
        Set<Class<?>> knownClasses = ImmutableSet.of();
        ThriftToReflectionLookup mockLookup = mock(ThriftToReflectionLookup.class);

        // when
        PageDefinitionFactory factory = factoryFactory.createPageDefinitionFactory(knownClasses, mockLookup);

        // then
        assertThat(factory, is(notNullValue()));
    }
}
