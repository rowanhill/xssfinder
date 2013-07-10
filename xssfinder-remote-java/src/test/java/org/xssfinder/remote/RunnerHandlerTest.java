package org.xssfinder.remote;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.scanner.PageDefinitionFactory;
import org.xssfinder.scanner.PageFinder;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RunnerHandlerTest {
    @Mock
    private PageFinder mockPageFinder;
    @Mock
    private PageDefinitionFactory mockPageDefFactory;
    @Mock
    private PageDefinition mockPageDefinition;

    @Test
    public void gettingPageDefinitionsDelegatesToPageFinderAndPageDefinitionFactory() throws Exception {
        // given
        Set<Class<?>> pageClasses = new HashSet<Class<?>>();
        pageClasses.add(SomePage.class);
        when(mockPageFinder.findAllPages("some.namespace")).thenReturn(pageClasses);
        when(mockPageDefFactory.createPageDefinition(SomePage.class, pageClasses)).thenReturn(mockPageDefinition);
        RunnerHandler runnerHandler = new RunnerHandler(mockPageFinder, mockPageDefFactory);

        // when
        Set<PageDefinition> pageDefinitions = runnerHandler.getPageDefinitions("some.namespace");

        // then
        Set<PageDefinition> expectedDefinitions = ImmutableSet.of(mockPageDefinition);
        assertThat(pageDefinitions, is(expectedDefinitions));
    }

    @Test
    public void gettingPageDefinitionsThrowsExceptionIfNoPageObjectsCanBeFound() throws Exception {
        // given
        PageFinder mockPageFinder = mock(PageFinder.class);
        Set<Class<?>> pageClasses = new HashSet<Class<?>>();
        when(mockPageFinder.findAllPages("some.namespace")).thenReturn(pageClasses);
        RunnerHandler runnerHandler = new RunnerHandler(mockPageFinder, mockPageDefFactory);

        // when
        runnerHandler.getPageDefinitions("some.namespace");
    }

    private static class SomePage {}
}
