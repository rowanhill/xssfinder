package org.xssfinder.runner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.*;
import org.xssfinder.routing.PageDescriptor;
import org.xssfinder.routing.PageTraversal;

import java.lang.reflect.Method;

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
    private LabelledXssGenerator mockLabelledXssGenerator;
    @Mock
    private PageDescriptor mockPageDescriptor;
    @Mock
    private PageTraversal mockTraversal;
    @Mock
    private CustomTraverser mockCustomTraverser;
    @Mock
    private CustomSubmitter mockCustomSubmitter;

    private PageTraverser traverser;

    @Before
    public void setUp() throws Exception {
        when(mockTraversal.getMethod()).thenReturn(RootPage.class.getMethod("goToSecondPage"));
        when(mockTraversal.getTraversalMode()).thenReturn(PageTraversal.TraversalMode.NORMAL);

        Method method = RootPage.class.getMethod("goToSecondPage");
        when(mockTraversal.getMethod()).thenReturn(method);

        when(mockLabelledXssGeneratorFactory.createLabelledXssGenerator(mockTraversal))
                .thenReturn(mockLabelledXssGenerator);

        traverser = new PageTraverser(mockTraverserInstantiator, mockSubmitterInstantiator, mockLabelledXssGeneratorFactory);
    }

    @Test
    public void invokesNoArgTraversalMethodAndReturnsResult() throws Exception {
        // given
        RootPage page = new RootPage();

        // when
        Object nextPage = traverser.traverse(page, mockTraversal);

        // then
        assertThat(nextPage, is(instanceOf(SecondPage.class)));
    }

    @Test(expected=UntraversableException.class)
    public void exceptionInvokingTraversalMethodGeneratesUntraversableException() throws Exception {
        // given
        setTraversalMethodThatRaisesException();
        RootPage page = new RootPage();

        // when
        traverser.traverse(page, mockTraversal);
    }

    @Test(expected=UntraversableException.class)
    public void tryingToTraverseMethodWithArgsGeneratesUntraversableException() throws Exception {
        // given
        setTraversalMethodThatHasParameter();
        RootPage page = new RootPage();

        // when
        traverser.traverse(page, mockTraversal);
    }

    @Test
    public void customTraverserIsCreatedToTraverseAnnotatedMethod() throws Exception {
        // given
        RootPage page = new RootPage();
        mockMethodAsHavingCustomTraverser();
        SecondPage mockSecondPage = mockCustomTraversedSecondPage(page);

        // when
        Object nextPage = traverser.traverse(page, mockTraversal);

        // then
        assertThat(nextPage, is((Object) mockSecondPage));
    }

    @Test
    public void customSubmitterIsCreatedToSubmitAnnotatedMethod() throws Exception {
        //given
        RootPage page = new RootPage();
        mockMethodAsHavingCustomSubmitter();
        SecondPage mockSecondPage = mockCustomSubmittedSecondPage(page);
        setSubmitTraversalMode();

        // when
        Object nextPage = traverser.traverse(page, mockTraversal);

        // then
        assertThat(nextPage, is((Object) mockSecondPage));
    }

    @Test(expected=UntraversableException.class)
    public void tryingToTraverseSubmitMethodWithArgsGeneratesUntraversableException() throws Exception {
        // given
        setTraversalMethodThatHasParameter();
        RootPage page = new RootPage();

        // when
        traverser.traverse(page, mockTraversal);
    }

    public void customTraverserIsUsedWhenTraversingMethodAnnotatedWithSubmitActionAndTraverseWith() throws Exception {
        // given
        RootPage page = new RootPage();
        mockMethodAsHavingCustomTraverser();
        SecondPage mockTraversedSecondPage = mockCustomTraversedSecondPage(page);
        mockMethodAsHavingCustomSubmitter();
        setSubmitTraversalMode();

        // when
        Object nextPage = traverser.traverse(page, mockTraversal);

        // then
        assertThat(nextPage, is((Object)mockTraversedSecondPage));
    }

    public void customSubmitterIsUsedWhenSubmittingMethodAnnotatedWithSubmitActionAndTraverseWith() throws Exception {
        // given
        RootPage page = new RootPage();
        mockMethodAsHavingCustomTraverser();
        mockMethodAsHavingCustomSubmitter();
        SecondPage mockSubmittedSecondPage = mockCustomSubmittedSecondPage(page);

        // when
        Object nextPage = traverser.traverse(page, mockTraversal);

        // then
        assertThat(nextPage, is((Object)mockSubmittedSecondPage));
    }

    private void setTraversalMethodThatRaisesException() throws Exception {
        Method method = RootPage.class.getMethod("raiseException");
        when(mockTraversal.getMethod()).thenReturn(method);
    }

    private void setTraversalMethodThatHasParameter() throws Exception {
        Method method = RootPage.class.getMethod("withParameter", String.class);
        when(mockTraversal.getMethod()).thenReturn(method);
    }

    private void mockMethodAsHavingCustomTraverser() {
        when(mockTraverserInstantiator.instantiate(any(Method.class))).thenReturn(mockCustomTraverser);
    }

    private void mockMethodAsHavingCustomSubmitter() {
        when(mockSubmitterInstantiator.instantiate(any(Method.class))).thenReturn(mockCustomSubmitter);
    }

    private void setSubmitTraversalMode() {
        when(mockTraversal.getTraversalMode()).thenReturn(PageTraversal.TraversalMode.SUBMIT);
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
