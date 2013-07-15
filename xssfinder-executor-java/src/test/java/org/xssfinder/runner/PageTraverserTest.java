package org.xssfinder.runner;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CustomSubmitter;
import org.xssfinder.CustomTraverser;
import org.xssfinder.Page;
import org.xssfinder.remote.TraversalMode;

import java.lang.reflect.Method;
import java.util.Map;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageTraverserTest {
    @Mock
    private CustomTraverserInstantiator mockTraverserInstantiator;
    @Mock
    private CustomSubmitterInstantiator mockSubmitterInstantiator;
    @Mock
    private LabelledXssGeneratorFactory mockLabelledXssGeneratorFactory;
    @Mock
    private LabelledXssGeneratorImpl mockLabelledXssGenerator;
    @Mock
    private CustomTraverser mockCustomTraverser;
    @Mock
    private CustomSubmitter mockCustomSubmitter;
    private Method method;

    private PageTraverser traverser;

    @Before
    public void setUp() throws Exception {
        method = RootPage.class.getMethod("goToSecondPage");

        when(mockLabelledXssGeneratorFactory.createLabelledXssGenerator()).thenReturn(mockLabelledXssGenerator);

        traverser = new PageTraverser(mockTraverserInstantiator, mockSubmitterInstantiator, mockLabelledXssGeneratorFactory);
    }

    @Test
    public void invokesNoArgTraversalMethodAndReturnsResult() throws Exception {
        // given
        RootPage page = new RootPage();

        // when
        TraversalResult result = traverser.traverse(page, method, TraversalMode.NORMAL);

        // then
        assertThat(result.getPage(), is(instanceOf(SecondPage.class)));
    }

    @Test(expected=UntraversableException.class)
    public void exceptionInvokingTraversalMethodGeneratesUntraversableException() throws Exception {
        // given
        setTraversalMethodThatRaisesException();
        RootPage page = new RootPage();

        // when
        traverser.traverse(page, method, TraversalMode.NORMAL);
    }

    @Test(expected=UntraversableException.class)
    public void tryingToTraverseMethodWithArgsGeneratesUntraversableException() throws Exception {
        // given
        setTraversalMethodThatHasParameter();
        RootPage page = new RootPage();

        // when
        traverser.traverse(page, method, TraversalMode.NORMAL);
    }

    @Test
    public void customTraverserIsCreatedToTraverseAnnotatedMethod() throws Exception {
        // given
        RootPage page = new RootPage();
        mockMethodAsHavingCustomTraverser();
        SecondPage mockSecondPage = mockCustomTraversedSecondPage(page);

        // when
        TraversalResult result = traverser.traverse(page, method, TraversalMode.NORMAL);

        // then
        assertThat(result.getPage(), is((Object) mockSecondPage));
    }

    @Test
    public void customSubmitterIsCreatedToSubmitAnnotatedMethod() throws Exception {
        //given
        RootPage page = new RootPage();
        mockMethodAsHavingCustomSubmitter();
        SecondPage mockSecondPage = mockCustomSubmittedSecondPage(page);

        // when
        TraversalResult result = traverser.traverse(page, method, TraversalMode.SUBMIT);

        // then
        assertThat(result.getPage(), is((Object) mockSecondPage));
    }

    @Test
    public void customSubmitterReturnsLabelToAttackIdMappingFromXssAttacksGeneratedWithLabelledXssGenerator() throws Exception {
        // given
        RootPage page = new RootPage();
        mockMethodAsHavingCustomSubmitter();
        Map<String, String> expectedLabelsToAttackIds = ImmutableMap.of("label", "attack ID");
        when(mockLabelledXssGenerator.getLabelsToAttackIds()).thenReturn(expectedLabelsToAttackIds);

        // when
        TraversalResult result = traverser.traverse(page, method, TraversalMode.SUBMIT);

        // then
        assertThat(result.getInputIdsToAttackIds(), is(expectedLabelsToAttackIds));
    }

    @Test(expected=UntraversableException.class)
    public void tryingToTraverseSubmitMethodWithArgsGeneratesUntraversableException() throws Exception {
        // given
        setTraversalMethodThatHasParameter();
        RootPage page = new RootPage();

        // when
        traverser.traverse(page, method, TraversalMode.NORMAL);
    }

    public void customTraverserIsUsedWhenTraversingMethodAnnotatedWithSubmitActionAndTraverseWith() throws Exception {
        // given
        RootPage page = new RootPage();
        mockMethodAsHavingCustomTraverser();
        SecondPage mockTraversedSecondPage = mockCustomTraversedSecondPage(page);
        mockMethodAsHavingCustomSubmitter();

        // when
        TraversalResult result = traverser.traverse(page, method, TraversalMode.SUBMIT);

        // then
        assertThat(result.getPage(), is((Object)mockTraversedSecondPage));
    }

    public void customSubmitterIsUsedWhenSubmittingMethodAnnotatedWithSubmitActionAndTraverseWith() throws Exception {
        // given
        RootPage page = new RootPage();
        mockMethodAsHavingCustomTraverser();
        mockMethodAsHavingCustomSubmitter();
        SecondPage mockSubmittedSecondPage = mockCustomSubmittedSecondPage(page);

        // when
        TraversalResult result = traverser.traverse(page, method, TraversalMode.NORMAL);

        // then
        assertThat(result.getPage(), is((Object)mockSubmittedSecondPage));
    }

    private void setTraversalMethodThatRaisesException() throws Exception {
        method = RootPage.class.getMethod("raiseException");
    }

    private void setTraversalMethodThatHasParameter() throws Exception {
        method = RootPage.class.getMethod("withParameter", String.class);
    }

    private void mockMethodAsHavingCustomTraverser() {
        when(mockTraverserInstantiator.instantiate(any(Method.class))).thenReturn(mockCustomTraverser);
    }

    private void mockMethodAsHavingCustomSubmitter() {
        when(mockSubmitterInstantiator.instantiate(any(Method.class))).thenReturn(mockCustomSubmitter);
    }

    private SecondPage mockCustomTraversedSecondPage(RootPage page) {
        SecondPage mockSecondPage = mock(SecondPage.class);
        when(mockCustomTraverser.traverse(page)).thenReturn(mockSecondPage);
        return mockSecondPage;
    }

    private SecondPage mockCustomSubmittedSecondPage(RootPage page) {
        SecondPage mockSecondPage = mock(SecondPage.class);
        when(mockCustomSubmitter.submit(page, mockLabelledXssGenerator)).thenReturn(mockSecondPage);
        return mockSecondPage;
    }

    @SuppressWarnings("UnusedDeclaration")
    @Page
    private static class RootPage {
        public SecondPage goToSecondPage() {
            return new SecondPage();
        }
        public SecondPage raiseException() {
            throw new RuntimeException();
        }
        public SecondPage withParameter(String dummy) {
            return new SecondPage();
        }
    }

    @Page
    private static class SecondPage {}
}
