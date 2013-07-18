package org.xssfinder.scanner;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ThriftToReflectionLookupTest {
    private static final String PAGE_ID = "SomePage";
    private static final String METHOD_ID = "someMethod";
    @Test
    public void pageMappingIsEmptyOnConstruction() {
        // given
        ThriftToReflectionLookup lookup = new ThriftToReflectionLookup();

        // when
        Class<?> pageClass = lookup.getPageClass(PAGE_ID);

        // then
        assertThat(pageClass, is(nullValue()));
    }

    @Test
    public void methodMappingIsEmptyOnConstruction() {
        // given
        ThriftToReflectionLookup lookup = new ThriftToReflectionLookup();

        // when
        Method method = lookup.getMethod(METHOD_ID);

        // then
        assertThat(method, is(nullValue()));
    }

    @Test
    public void setPageClassIsAvailableFromLookup() {
        // given
        ThriftToReflectionLookup lookup = new ThriftToReflectionLookup();
        lookup.putPageClass(PAGE_ID, SomePage.class);

        // when
        Class<?> pageClass = lookup.getPageClass(PAGE_ID);

        // then
        assertEquals(pageClass, SomePage.class);
    }

    @Test
    public void setMethodIsAvailableFromLookup() throws Exception {
        // given
        ThriftToReflectionLookup lookup = new ThriftToReflectionLookup();
        Method goToSomePage = SomePage.class.getMethod("goToSomePage");
        lookup.putMethod(METHOD_ID, goToSomePage);

        // when
        Method method = lookup.getMethod(METHOD_ID);

        // then
        assertEquals(method, goToSomePage);
    }

    private static class SomePage {
        public SomePage goToSomePage() { return null; }
    }
}
