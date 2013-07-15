package org.xssfinder.runner;

import org.junit.Test;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PageDefinitionMappingTest {
    @Test
    public void pageDefinitionAndPageClassAndMethodDefinitionMappingsAreAvailable() {
        // given
        PageDefinition mockPageDefinition = mock(PageDefinition.class);
        Map<MethodDefinition, Method> expectedMethods = new HashMap<MethodDefinition, Method>();

        // when
        PageDefinitionMapping mapping = new PageDefinitionMapping(SomePage.class, mockPageDefinition, expectedMethods);

        // then
        assertThat(mapping.getPageClass() == SomePage.class, is(true));
        assertThat(mapping.getPageDefinition(), is(mockPageDefinition));
        assertThat(mapping.getMethodMapping(), is(expectedMethods));
    }

    private static class SomePage {}
}
