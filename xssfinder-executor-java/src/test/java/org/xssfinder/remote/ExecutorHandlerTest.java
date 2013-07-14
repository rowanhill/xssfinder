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
public class ExecutorHandlerTest {
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
        ExecutorHandler executorHandler = new ExecutorHandler(mockPageFinder, mockPageDefFactory);

        // when
        Set<PageDefinition> pageDefinitions = executorHandler.getPageDefinitions("some.namespace");

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
        ExecutorHandler executorHandler = new ExecutorHandler(mockPageFinder, mockPageDefFactory);

        // when
        executorHandler.getPageDefinitions("some.namespace");
    }

        /*
    @Test
    public void createsLifecycleHandler() throws Exception {
        // given
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockPageTraversalFactory);
        LifecycleHandler mockHandler = mock(LifecycleHandler.class);
        when(mockInstantiator.instantiate(LifecycleHandler.class)).thenReturn(mockHandler);

        // when
        Object handler = route.createLifecycleHandler();

        // then
        assertThat(handler, is((Object)mockHandler));
    }

    @Test(expected=LifecycleEventException.class)
    public void throwsExceptionIfCreatingLifecycleHandlerFails() throws Exception {
        // given
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockPageTraversalFactory);
        when(mockInstantiator.instantiate(LifecycleHandler.class)).thenThrow(new InstantiationException(null));

        // when
        route.createLifecycleHandler();
    }

    @Test
    public void createsNullLifecycleHandlerIfNoneSpecified() throws Exception {
        // given
        //noinspection unchecked
        when(mockPageDescriptor.getPageClass()).thenReturn((Class) PageWithoutLifecycleHandler.class);
        Route route = new Route(mockPageDescriptor, mockPageTraversal, mockPageTraversalFactory);

        // when
        Object handler = route.createLifecycleHandler();

        // then
        assertThat(handler, is(nullValue()));
        //noinspection unchecked
        verify(never()).instantiate(any(Class.class));
    }
    */

    private static class SomePage {}
}
