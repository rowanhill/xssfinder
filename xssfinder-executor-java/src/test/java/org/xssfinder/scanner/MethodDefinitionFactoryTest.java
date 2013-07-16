package org.xssfinder.scanner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CustomTraverser;
import org.xssfinder.SubmitAction;
import org.xssfinder.TraverseWith;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;
import org.xssfinder.runner.PageDefinitionMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MethodDefinitionFactoryTest {
    private static final String PAGE_DEF_ID = "A Page";

    private final MethodDefinitionFactory factory = new MethodDefinitionFactory();
    private final Map<Class<?>, PageDefinitionMapping> pageDefinitionCache = new HashMap<Class<?>, PageDefinitionMapping>();
    private final Set<Class<?>> knownPageClasses = new HashSet<Class<?>>();

    @Mock
    private PageDefinitionFactory mockPageDefinitionFactory;
    @Mock
    private PageDefinitionMapping mockPageDefinitionMapping;
    @Mock
    private PageDefinition mockPageDefinition;

    @Before
    public void setUp() {
        when(mockPageDefinition.getIdentifier()).thenReturn(PAGE_DEF_ID);
        when(mockPageDefinitionMapping.getPageDefinition()).thenReturn(mockPageDefinition);
        pageDefinitionCache.put(SomePage.class, mockPageDefinitionMapping);
    }

    @Test
    public void definitionIdentifierIsMethodName() throws Exception {
        // given
        Method method = SomePage.class.getMethod("goToSomePage");

        // when
        MethodDefinition methodDefinition =
                factory.createMethodDefinition(method, pageDefinitionCache, mockPageDefinitionFactory, knownPageClasses);

        // then
        assertThat(methodDefinition.getIdentifier(), is(method.getName()));
    }

    @Test
    public void returnTypeIsMethodReturnType() throws Exception {
        // given
        pageDefinitionCache.put(SomePage.class, mockPageDefinitionMapping);
        Method method = SomePage.class.getMethod("goToSomePage");

        // when
        MethodDefinition methodDefinition =
                factory.createMethodDefinition(method, pageDefinitionCache, mockPageDefinitionFactory, knownPageClasses);

        // then
        assertThat(methodDefinition.getReturnTypeIdentifier(), is(PAGE_DEF_ID));
    }

    @Test
    public void returnTypePageDefinitionIsCreatedIfNotInCache() throws Exception {
        // given
        pageDefinitionCache.clear();
        when(mockPageDefinitionFactory.createPageDefinition(SomePage.class, knownPageClasses))
                .thenReturn(mockPageDefinitionMapping);
        Method method = SomePage.class.getMethod("goToSomePage");

        // when
        MethodDefinition methodDefinition =
                factory.createMethodDefinition(method, pageDefinitionCache, mockPageDefinitionFactory, knownPageClasses);

        // then
        assertThat(methodDefinition.getReturnTypeIdentifier(), is(PAGE_DEF_ID));
    }

    @Test
    public void definitionHasArgumentsIfMethodHasArguments() throws Exception {
        // given
        Method method = SomePage.class.getMethod("goToSomePageWithArgs", String.class);

        // when
        MethodDefinition methodDefinition =
                factory.createMethodDefinition(method, pageDefinitionCache, mockPageDefinitionFactory, knownPageClasses);

        // then
        assertThat(methodDefinition.isParameterised(), is(true));
    }

    @Test
    public void definitionDoesNotHaveArgumentsIfMethodDoesNotHaveArguments() throws Exception {
        // given
        Method method = SomePage.class.getMethod("goToSomePage");

        // when
        MethodDefinition methodDefinition =
                factory.createMethodDefinition(method, pageDefinitionCache, mockPageDefinitionFactory, knownPageClasses);

        // then
        assertThat(methodDefinition.isParameterised(), is(false));
    }

    @Test
    public void definitionIsSubmitAnnotatedIfMethodHasSubmitActionAnnotation() throws Exception {
        // given
        Method method = SomePage.class.getMethod("submit");

        // when
        MethodDefinition methodDefinition =
                factory.createMethodDefinition(method, pageDefinitionCache, mockPageDefinitionFactory, knownPageClasses);

        // then
        assertThat(methodDefinition.isSubmitAnnotated(), is(true));
    }

    @Test
    public void definitionIsNotSubmitAnnotatedIfMethodDoesNotHaveSubmitActionAnnotation() throws Exception {
        // given
        Method method = SomePage.class.getMethod("goToSomePage");

        // when
        MethodDefinition methodDefinition =
                factory.createMethodDefinition(method, pageDefinitionCache, mockPageDefinitionFactory, knownPageClasses);

        // then
        assertThat(methodDefinition.isSubmitAnnotated(), is(false));
    }

    @Test
    public void definitionIsCustomTraversedIfMethodHasTraverseWithAnnotation() throws Exception {
        // given
        Method method = SomePage.class.getMethod("traverse");

        // when
        MethodDefinition methodDefinition =
                factory.createMethodDefinition(method, pageDefinitionCache, mockPageDefinitionFactory, knownPageClasses);

        // then
        assertThat(methodDefinition.isCustomTraversed(), is(true));
    }

    @Test
    public void definitionIsNotCustomTraversedIfMethodDoesNotHaveTraverseWithAnnotation() throws Exception {
        // given
        Method method = SomePage.class.getMethod("goToSomePage");

        // when
        MethodDefinition methodDefinition =
                factory.createMethodDefinition(method, pageDefinitionCache, mockPageDefinitionFactory, knownPageClasses);

        // then
        assertThat(methodDefinition.isCustomTraversed(), is(false));
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class SomePage {
        public SomePage goToSomePage() { return null; }
        public SomePage goToSomePageWithArgs(String dummy) { return null; }
        @SubmitAction
        public SomePage submit() { return null; }
        @TraverseWith(CustomTraverser.class)
        public SomePage traverse() { return  null; }
    }
}
