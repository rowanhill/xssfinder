package org.xssfinder.remote;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.runner.ExecutorContext;
import org.xssfinder.runner.TraversalResult;
import org.xssfinder.scanner.PageDefinitionFactory;
import org.xssfinder.scanner.PageFinder;
import org.xssfinder.scanner.ThriftToReflectionLookup;
import org.xssfinder.scanner.ThriftToReflectionLookupFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorHandlerTest {
    private static final String PACKAGE_NAME = "some.namespace";
    private static final String PAGE_ID = "Page ID";

    @Mock
    private PageFinder mockPageFinder;
    @Mock
    private PageDefinitionFactory mockPageDefFactory;
    @Mock
    private ThriftToReflectionLookupFactory mockThriftToReflectionLookupFactory;
    @Mock
    private ExecutorContext mockExecutorContext;
    @Mock
    private PageDefinition mockPageDefinition;
    @Mock
    private ThriftToReflectionLookup mockLookup = new ThriftToReflectionLookup();

    @InjectMocks
    private ExecutorHandler executorHandler;

    @Before
    public void setUp() {
        when(mockThriftToReflectionLookupFactory.createLookup()).thenReturn(mockLookup);
    }

    @Test
    public void gettingPageDefinitionsDelegatesToPageFinderAndPageDefinitionFactory() throws Exception {
        // given
        Set<Class<?>> pageClasses = new HashSet<Class<?>>();
        pageClasses.add(SomePage.class);
        when(mockPageFinder.findAllPages(PACKAGE_NAME)).thenReturn(pageClasses);
        when(mockPageDefFactory.createPageDefinition(SomePage.class, pageClasses, mockLookup)).thenReturn(mockPageDefinition);

        // when
        Set<PageDefinition> pageDefinitions = executorHandler.getPageDefinitions(PACKAGE_NAME);

        // then
        Set<PageDefinition> expectedDefinitions = ImmutableSet.of(mockPageDefinition);
        assertThat(pageDefinitions, is(expectedDefinitions));
    }

    @Test
    public void gettingPageDefinitionsSetsContextLookup() throws Exception {
        // when
        executorHandler.getPageDefinitions(PACKAGE_NAME);

        // then
        verify(mockExecutorContext).setThriftToReflectionLookup(mockLookup);
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
    public void visitingUrlIsDelegatedToExecutorContext() throws Exception {
        // when
        executorHandler.startRoute("pageId");

        // then
        verify(mockExecutorContext).visitUrlOfRootPage("pageId");
    }

    @Test
    public void puttingXssAttackStringsInInputsIsDelegatedToExecutorContext() throws Exception {
        // given
        Map<String, String> givenInputIdsToAttackIds = ImmutableMap.of("foo", "bar");
        when(mockExecutorContext.putXssAttackStringsInInputs()).thenReturn(givenInputIdsToAttackIds);

        // when
        Map<String, String> inputIdsToAttackIds = executorHandler.putXssAttackStringsInInputs();

        // then
        assertThat(inputIdsToAttackIds, is(givenInputIdsToAttackIds));
    }

    @Test
    public void gettingCurrentXssIdsIsDelegatedToExecutorContext() throws Exception {
        // given
        Set<String> givenIds = ImmutableSet.of("foo");
        when(mockExecutorContext.getCurrentXssIds()).thenReturn(givenIds);

        // when
        Set<String> xssIds = executorHandler.getCurrentXssIds();

        // then
        assertThat(xssIds, is(givenIds));
    }

    @Test
    public void gettingFormCountIsDelegatedToExecutorContext() throws Exception {
        // given
        int givenFormCount = 3;
        when(mockExecutorContext.getFormCount()).thenReturn(givenFormCount);

        // when
        int formCount =  executorHandler.getFormCount();

        // then
        assertThat(formCount, is(givenFormCount));
    }

    @Test
    public void traversingMethodIsDelegatedToExecutorContext() throws Exception {
        // given
        MethodDefinition mockMethodDefinition = mock(MethodDefinition.class);
        TraversalResult mockResult = mock(TraversalResult.class);
        Map<String, String> expectedInputIdsToXssIds = new HashMap<String, String>();
        when(mockResult.getInputIdsToAttackIds()).thenReturn(expectedInputIdsToXssIds);
        when(mockExecutorContext.traverseMethod(mockMethodDefinition, TraversalMode.NORMAL)).thenReturn(mockResult);

        // when
        Map<String, String> inputIdsToXssIds = executorHandler.traverseMethod(mockMethodDefinition, TraversalMode.NORMAL);

        // then
        assertThat(inputIdsToXssIds, is(expectedInputIdsToXssIds));
    }

    @Test
    public void invokingAfterRouteHandlerDelegatesToExecutorContext() throws Exception {
        // when
        executorHandler.invokeAfterRouteHandler(PAGE_ID);

        // then
        verify(mockExecutorContext).invokeAfterRouteHandler(PAGE_ID);
    }

    private static class SomePage {
        @SuppressWarnings("UnusedDeclaration")
        public SomePage goToSomePage() { return null; }
    }
}
