package org.xssfinder.remote;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.runner.DriverWrapper;
import org.xssfinder.scanner.PageDefinitionFactory;
import org.xssfinder.scanner.PageFinder;
import org.xssfinder.xss.XssGenerator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorHandlerTest {
    public static final String PACKAGE_NAME = "some.namespace";
    @Mock
    private PageFinder mockPageFinder;
    @Mock
    private PageDefinitionFactory mockPageDefFactory;
    @Mock
    private PageDefinition mockPageDefinition;
    @Mock
    private DriverWrapper mockDriverWrapper;
    @Mock
    private XssGenerator mockXssGenerator;

    @InjectMocks
    private ExecutorHandler executorHandler;

    @Test
    public void gettingPageDefinitionsDelegatesToPageFinderAndPageDefinitionFactory() throws Exception {
        // given
        Set<Class<?>> pageClasses = new HashSet<Class<?>>();
        pageClasses.add(SomePage.class);
        when(mockPageFinder.findAllPages(PACKAGE_NAME)).thenReturn(pageClasses);
        when(mockPageDefFactory.createPageDefinition(SomePage.class, pageClasses)).thenReturn(mockPageDefinition);

        // when
        Set<PageDefinition> pageDefinitions = executorHandler.getPageDefinitions(PACKAGE_NAME);

        // then
        Set<PageDefinition> expectedDefinitions = ImmutableSet.of(mockPageDefinition);
        assertThat(pageDefinitions, is(expectedDefinitions));
    }

    @Test
    public void gettingPageDefinitionsThrowsExceptionIfNoPageObjectsCanBeFound() throws Exception {
        // given
        Set<Class<?>> pageClasses = new HashSet<Class<?>>();
        when(mockPageFinder.findAllPages(PACKAGE_NAME)).thenReturn(pageClasses);

        // when
        executorHandler.getPageDefinitions(PACKAGE_NAME);
    }

    @Test
    public void visitingUrlIsDelegatedToDriverWrapper() throws Exception {
        // when
        executorHandler.visit("http://www.google.com");

        // then
        verify(mockDriverWrapper).visit("http://www.google.com");
    }

    @Test
    public void puttingXssAttackStringsInInputsIsDelegatedToDriverWrapper() throws Exception {
        // given
        Map<String, String> givenInputIdsToAttackIds = ImmutableMap.of("foo", "bar");
        when(mockDriverWrapper.putXssAttackStringsInInputs(mockXssGenerator)).thenReturn(givenInputIdsToAttackIds);

        // when
        Map<String, String> inputIdsToAttackIds = executorHandler.putXssAttackStringsInInputs();

        // then
        assertThat(inputIdsToAttackIds, is(givenInputIdsToAttackIds));
    }

    @Test
    public void gettingCurrentXssIdsIsDelegatedToDriverWrapper() throws Exception {
        // given
        Set<String> givenIds = ImmutableSet.of("foo");
        when(mockDriverWrapper.getCurrentXssIds()).thenReturn(givenIds);

        // when
        Set<String> xssIds = executorHandler.getCurrentXssIds();

        // then
        assertThat(xssIds, is(givenIds));
    }


    @Test
    public void gettingFormCountIsDelegatedToDriverWrapper() throws Exception {
        // given
        int givenFormCount = 3;
        when(mockDriverWrapper.getFormCount()).thenReturn(givenFormCount);

        // when
        int formCount =  executorHandler.getFormCount();

        // then
        assertThat(formCount, is(givenFormCount));
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
