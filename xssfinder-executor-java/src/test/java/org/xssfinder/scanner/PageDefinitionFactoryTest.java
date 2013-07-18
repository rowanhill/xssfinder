package org.xssfinder.scanner;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PageDefinitionFactoryTest {
    @Mock
    private MethodDefinitionFactory mockMethodDefinitionFactory;
    @Mock
    private MethodDefinition mockMethodDefinition;
    @Mock
    private ThriftToReflectionLookup mockLookup;
    private Set<Class<?>> knownPageClasses;

    private PageDefinitionFactory factory;

    @Before
    public void setUp() {
        factory = new PageDefinitionFactory(mockMethodDefinitionFactory, new HashMap<Class<?>, PageDefinition>());
        knownPageClasses = ImmutableSet.of(
                SomePage.class,
                NoPageReturningPage.class,
                LinkingPage.class,
                CircularReferencePage.class
        );
    }

    @Test
    public void definitionIdentifierIsFullyQualifiedClassName() {
        // when
        PageDefinition pageDefinition = factory.createPageDefinition(SomePage.class, knownPageClasses, mockLookup);

        // then
        assertThat(pageDefinition.getIdentifier(), is(SomePage.class.getCanonicalName()));
    }

    @Test
    public void definitionIsAddedToLookup() {
        // when
        PageDefinition pageDefinition = factory.createPageDefinition(SomePage.class, knownPageClasses, mockLookup);

        // then
        verify(mockLookup).putPageClass(pageDefinition.getIdentifier(), SomePage.class);
    }

    @Test
    public void definitionMethodsIsEmptySetIfClassHasNoMethods() {
        // when
        PageDefinition pageDefinition = factory.createPageDefinition(SomePage.class, knownPageClasses, mockLookup);

        // then
        assertThat(pageDefinition.getMethods(), is(empty()));
    }

    @Test
    public void definitionMethodsIsEmptySetIfClassHasNoMethodsReturningPageObjects() {
        // when
        PageDefinition pageDefinition = factory.createPageDefinition(NoPageReturningPage.class, knownPageClasses, mockLookup);

        // then
        assertThat(pageDefinition.getMethods(), is(empty()));
    }

    @Test
    public void definitionMethodsHasMethodDefinitionForMethodsOnClassWhichReturnPageObjects() throws Exception {
        // given
        Method method = LinkingPage.class.getMethod("goToSomePage");
        setUpMockForMethod(method);

        // when
        PageDefinition pageDefinition = factory.createPageDefinition(LinkingPage.class, knownPageClasses, mockLookup);

        // then
        Set<MethodDefinition> expectedMethodDefinitions = ImmutableSet.of(mockMethodDefinition);
        assertThat(pageDefinition.getMethods(), is(expectedMethodDefinitions));
    }

    @Test
    public void methodDefinitionsFromPageAreAddedToLookup() throws Exception {
        // given
        Method method = LinkingPage.class.getMethod("goToSomePage");
        setUpMockForMethod(method);
        when(mockMethodDefinition.getIdentifier()).thenReturn("mockGoToSomePage");

        // when
        factory.createPageDefinition(LinkingPage.class, knownPageClasses, mockLookup);

        // then
        verify(mockLookup).putMethod("mockGoToSomePage", method);
    }

    @Test
    public void definitionIsCrawlStartPointIfPageIsAnnotatedWithCrawlStartPoint() throws Exception {
        // when
        PageDefinition pageDefinition = factory.createPageDefinition(HomePage.class, knownPageClasses, mockLookup);

        // then
        assertThat(pageDefinition.isCrawlStartPoint(), is(true));
    }

    @Test
    public void definitionIsNotCrawlStartPointIfPageIsNotAnnotatedWithCrawlStartPoint() throws Exception {
        // when
        PageDefinition pageDefinition = factory.createPageDefinition(SomePage.class, knownPageClasses, mockLookup);

        // then
        assertThat(pageDefinition.isCrawlStartPoint(), is(false));
    }

    @Test
    public void pageDefinitionIsInCacheBeforeCreatingMethodDefinitions() throws Exception {
        //given
        //noinspection unchecked
        HashMap<Class<?>, PageDefinition> mockPageDefinitionCache = mock(HashMap.class);
        factory = new PageDefinitionFactory(mockMethodDefinitionFactory, mockPageDefinitionCache);
        Method method = CircularReferencePage.class.getMethod("goToSelf");
        setUpMockForMethod(method);

        // when
        factory.createPageDefinition(CircularReferencePage.class, knownPageClasses, mockLookup);

        // then
        InOrder inOrder = inOrder(mockMethodDefinitionFactory, mockPageDefinitionCache);
        inOrder.verify(mockPageDefinitionCache).put(eq(CircularReferencePage.class), any(PageDefinition.class));
        inOrder.verify(mockMethodDefinitionFactory, times(1)).createMethodDefinition(
                eq(method),
                eq(factory),
                eq(knownPageClasses),
                eq(mockLookup)
        );
        verifyNoMoreInteractions(mockMethodDefinitionFactory);
    }

    private void setUpMockForMethod(Method method) {
        //noinspection unchecked
        when(mockMethodDefinitionFactory.createMethodDefinition(
                eq(method),
                eq(factory),
                eq(knownPageClasses),
                eq(mockLookup)
        )).thenReturn(mockMethodDefinition);
    }

    private static class SomePage {}

    @SuppressWarnings("UnusedDeclaration")
    private static class NoPageReturningPage {
        public String notATraversal() { return null; }
        public void notATraversalEither() { }
    }

    private static class LinkingPage {
        public SomePage goToSomePage() { return null; }
    }

    @CrawlStartPoint(url = "some-url")
    private static class HomePage {}


    @SuppressWarnings("UnusedDeclaration")
    private static class CircularReferencePage {
        public CircularReferencePage goToSelf() { return null; }
    }
}
