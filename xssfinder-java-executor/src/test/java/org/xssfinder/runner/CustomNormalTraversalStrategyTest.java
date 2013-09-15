package org.xssfinder.runner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CustomTraverser;
import org.xssfinder.Page;
import org.xssfinder.remote.TraversalMode;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomNormalTraversalStrategyTest
{
    @Mock
    private CustomTraverserInstantiator mockTraverserInstantiator;
    @Mock
    private CustomTraverser mockCustomTraverser;
    private Method method;

    private CustomNormalTraversalStrategy strategy;

    @Before
    public void setUp() throws Exception {
        method = RootPage.class.getMethod("goToSecondPage");
        strategy = new CustomNormalTraversalStrategy(mockTraverserInstantiator);
    }

    @Test
    public void cannotSatisfySubmissionTraversalMode() {
        // when
        boolean canSatisfy = strategy.canSatisfyMethod(method, TraversalMode.SUBMIT);

        // then
        assertThat(canSatisfy, is(false));
    }

    @Test
    public void cannotSatisfyNormalTraversalModeIfTraverserInstantiatorCannotProduceTraverser() {
        // when
        boolean canSatisfy = strategy.canSatisfyMethod(method, TraversalMode.NORMAL);

        // then
        assertThat(canSatisfy, is(false));
    }

    @Test
    public void canSatisfyNormalTraversalModeIfTraverserInstantiatorProducesTraverser() {
        // given
        mockMethodAsHavingCustomTraverser();

        // when
        boolean canSatisfy = strategy.canSatisfyMethod(method, TraversalMode.NORMAL);

        // then
        assertThat(canSatisfy, is(true));
    }

    @Test
    public void traversingIsDelegatedToCustomTraverser() {
        // given
        RootPage page = new RootPage();
        mockMethodAsHavingCustomTraverser();
        SecondPage mockSecondPage = mockCustomTraversedSecondPage(page);

        // when
        TraversalResult result = strategy.traverse(page, method);

        // then
        assertThat(result.getPage(), is((Object) mockSecondPage));
    }

    private void mockMethodAsHavingCustomTraverser() {
        when(mockTraverserInstantiator.instantiate(any(Method.class))).thenReturn(mockCustomTraverser);
    }

    private SecondPage mockCustomTraversedSecondPage(RootPage page) {
        SecondPage mockSecondPage = mock(SecondPage.class);
        when(mockCustomTraverser.traverse(page)).thenReturn(mockSecondPage);
        return mockSecondPage;
    }

    @SuppressWarnings("UnusedDeclaration")
    @Page
    private class RootPage {
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
    private class SecondPage {}
}
