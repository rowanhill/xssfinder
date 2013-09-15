package org.xssfinder.runner;

import org.junit.Before;
import org.junit.Test;
import org.xssfinder.Page;
import org.xssfinder.remote.TUntraversableException;
import org.xssfinder.remote.TraversalMode;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SimpleMethodTraversalStrategyTest {
    private Method method;

    private SimpleMethodTraversalStrategy strategy;

    @Before
    public void setUp() throws Exception {
        method = RootPage.class.getMethod("goToSecondPage");
        strategy = new SimpleMethodTraversalStrategy();
    }

    @Test
    public void canSatisfyNormalTraversalMode() {
        // when
        boolean canSatisfy = strategy.canSatisfyMethod(method, TraversalMode.NORMAL);

        // then
        assertThat(canSatisfy, is(true));
    }

    @Test
    public void canSatisfySubmitTraversalMode() {
        // when
        boolean canSatisfy = strategy.canSatisfyMethod(method, TraversalMode.SUBMIT);

        // then
        assertThat(canSatisfy, is(true));
    }

    @Test
    public void traversingInvokesMethod() throws Exception {
        // given
        RootPage rootPage = new RootPage();

        // when
        TraversalResult traversalResult = strategy.traverse(rootPage, method);

        // then
        assertThat(traversalResult.getPage(), is(instanceOf(SecondPage.class)));
    }

    @Test(expected=RuntimeException.class)
    public void traversingMethodThatEncountersExceptionDoesNotCatchException() throws Exception {
        // given
        RootPage rootPage = new RootPage();
        method = RootPage.class.getMethod("raiseException");

        // when
        strategy.traverse(rootPage, method);
    }

    @Test(expected=TUntraversableException.class)
    public void traversingMethodWithArgumentsThrowsTUntraversableException() throws Exception {
        // given
        RootPage rootPage = new RootPage();
        method = RootPage.class.getMethod("withParameter", String.class);

        // when
        strategy.traverse(rootPage, method);
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
