package org.xssfinder.runner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.AfterRoute;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LifecycleEventExecutorTest {
    @Mock
    private SomePage mockPage;

    @Test(expected=LifecycleEventException.class)
    public void throwsExceptionIfMultipleAfterRouteMethodsDefined() {
        // given
        LifecycleEventExecutor eventExecutor = new LifecycleEventExecutor();
        Object handler = new MultipleAfterRouteHandler();

        // when
        eventExecutor.afterRoute(handler, mockPage);
    }

    @Test(expected=LifecycleEventException.class)
    public void throwsExceptionIfSignatureIsIncorrect() {
        // given
        LifecycleEventExecutor eventExecutor = new LifecycleEventExecutor();
        BadSignatureHandler handler = new BadSignatureHandler();

        // when
        eventExecutor.afterRoute(handler, mockPage);
    }

    @Test(expected=LifecycleEventException.class)
    public void throwsExceptionIfWrongPageIsInSignature() {
        // given
        LifecycleEventExecutor eventExecutor = new LifecycleEventExecutor();
        WrongClassSignatureHandler handler = new WrongClassSignatureHandler();

        // when
        eventExecutor.afterRoute(handler, mockPage);
    }

    @Test
    public void invokesAnnotatedMethodOnHandler() {
        // given
        LifecycleEventExecutor eventExecutor = new LifecycleEventExecutor();
        // Note: we have to use a real object rather than a mock, as annotations
        // on methods are not inherited.
        LifecycleHandler handler = new LifecycleHandler();

        // when
        eventExecutor.afterRoute(handler, mockPage);

        // then
        assertThat(handler.afterRouteInvoked, is(true));
    }

    private static class SomePage {}
    private static class SomeOtherPage {}

    private static class MultipleAfterRouteHandler {
        @AfterRoute
        public void afterRoute1(SomePage page) {}
        @AfterRoute
        public void afterRoute2(SomePage page) {}
    }

    private static class BadSignatureHandler {
        @AfterRoute
        public void afterRoute() {}
    }

    private static class WrongClassSignatureHandler {
        @AfterRoute
        public void afterRoute(SomeOtherPage page) {}
    }

    private class LifecycleHandler {
        private boolean afterRouteInvoked = false;
        @AfterRoute
        public void afterRoute(SomePage page) {
            if (page == mockPage) {
                afterRouteInvoked = true;
            }
        }
    }
}
