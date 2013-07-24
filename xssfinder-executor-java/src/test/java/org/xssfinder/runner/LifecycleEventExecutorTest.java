package org.xssfinder.runner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.AfterRoute;
import org.xssfinder.remote.TLifecycleEventHandlerException;
import org.xssfinder.remote.TWebInteractionException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class LifecycleEventExecutorTest {
    @Mock
    private SomePage mockPage;

    @Test(expected=TLifecycleEventHandlerException.class)
    public void throwsExceptionIfMultipleAfterRouteMethodsDefined() throws Exception {
        // given
        LifecycleEventExecutor eventExecutor = new LifecycleEventExecutor();
        Object handler = new MultipleAfterRouteHandler();

        // when
        eventExecutor.afterRoute(handler, mockPage);
    }

    @Test(expected=TLifecycleEventHandlerException.class)
    public void throwsExceptionIfSignatureIsIncorrect() throws Exception {
        // given
        LifecycleEventExecutor eventExecutor = new LifecycleEventExecutor();
        BadSignatureHandler handler = new BadSignatureHandler();

        // when
        eventExecutor.afterRoute(handler, mockPage);
    }

    @Test(expected=TLifecycleEventHandlerException.class)
    public void throwsExceptionIfWrongPageIsInSignature() throws Exception {
        // given
        LifecycleEventExecutor eventExecutor = new LifecycleEventExecutor();
        WrongClassSignatureHandler handler = new WrongClassSignatureHandler();

        // when
        eventExecutor.afterRoute(handler, mockPage);
    }

    @Test(expected=TLifecycleEventHandlerException.class)
    public void throwsExceptionIfAfterRouteMethodIsPrivate() throws Exception {
        // given
        LifecycleEventExecutor eventExecutor = new LifecycleEventExecutor();
        PrivateLifecycleHandler handler = new PrivateLifecycleHandler();

        // when
        eventExecutor.afterRoute(handler, mockPage);
    }

    @Test(expected=TWebInteractionException.class)
    public void throwsExceptionIfHandlerThrowsException() throws Exception {
        // given
        LifecycleEventExecutor eventExecutor = new LifecycleEventExecutor();
        ErrorThrowingLifecycleHandler handler = new ErrorThrowingLifecycleHandler();

        // when
        eventExecutor.afterRoute(handler, mockPage);
    }

    @Test
    public void invokesAnnotatedMethodOnHandler() throws Exception {
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

    @Test
    public void invokingAfterRouteDoesNothingIfLifecycleHandlerIsNull() throws Exception {
        // given
        LifecycleEventExecutor eventExecutor = new LifecycleEventExecutor();

        // when
        eventExecutor.afterRoute(null, mockPage);

        // then
        // No exception is thrown
        verifyZeroInteractions(mockPage);
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

    private static class ErrorThrowingLifecycleHandler {
        @AfterRoute
        public void afterRoute(SomePage page) {
            throw new RuntimeException("It went wrong");
        }
    }

    private static class PrivateLifecycleHandler {
        @AfterRoute
        private void afterRoute(SomeOtherPage page) {}
    }
}
