package org.xssfinder.scanner;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.lang.reflect.Method;
import java.util.Set;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageDefinitionFactoryTest {
    @Mock
    private MethodDefinitionFactory mockMethodDefinitionFactory;
    @Mock
    private MethodDefinition mockMethodDefinition;
    private Set<Class<?>> knownPageClasses;

    private PageDefinitionFactory factory;

    @Before
    public void setUp() {
        factory = new PageDefinitionFactory(mockMethodDefinitionFactory);
        knownPageClasses = ImmutableSet.of(
                SomePage.class,
                NoPageReturningPage.class,
                LinkingPage.class
        );
    }

    @Test
    public void definitionIdentifierIsFullyQualifiedClassName() {
        // when
        PageDefinition pageDefinition = factory.createPageDefinition(SomePage.class, knownPageClasses);

        // then
        assertThat(pageDefinition.getIdentifier(), is(SomePage.class.getCanonicalName()));
    }

    @Test
    public void definitionMethodsIsEmptySetIfClassHasNoMethods() {
        // when
        PageDefinition pageDefinition = factory.createPageDefinition(SomePage.class, knownPageClasses);

        // then
        assertThat(pageDefinition.getMethods(), is(empty()));
    }

    @Test
    public void definitionMethodsIsEmptySetIfClassHasNoMethodsReturnPageObjects() {
        // when
        PageDefinition pageDefinition = factory.createPageDefinition(NoPageReturningPage.class, knownPageClasses);

        // then
        assertThat(pageDefinition.getMethods(), is(empty()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void definitionMethodsHasMethodDefinitionForMethodsOnClassWhichReturnPageObjects() throws Exception {
        // given
        Method method = LinkingPage.class.getMethod("goToSomePage");
        when(mockMethodDefinitionFactory.createMethodDefinition(eq(method), anyMap(), eq(factory), eq(knownPageClasses)))
                .thenReturn(mockMethodDefinition);

        // when
        PageDefinition pageDefinition = factory.createPageDefinition(LinkingPage.class, knownPageClasses);

        // then
        Set<MethodDefinition> expectedMethodDefinitions = ImmutableSet.of(mockMethodDefinition);
        assertThat(pageDefinition.getMethods(), is(expectedMethodDefinitions));
    }

    @Test
    public void definitionIsCrawlStartPointIfPageIsAnnotatedWithCrawlStartPoint() throws Exception {
        // when
        PageDefinition pageDefinition = factory.createPageDefinition(HomePage.class, knownPageClasses);

        // then
        assertThat(pageDefinition.isCrawlStartPoint(), is(true));
    }

    @Test
    public void definitionIsNotCrawlStartPointIfPageIsNotAnnotatedWithCrawlStartPoint() throws Exception {
        // when
        PageDefinition pageDefinition = factory.createPageDefinition(SomePage.class, knownPageClasses);

        // then
        assertThat(pageDefinition.isCrawlStartPoint(), is(false));
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
}
