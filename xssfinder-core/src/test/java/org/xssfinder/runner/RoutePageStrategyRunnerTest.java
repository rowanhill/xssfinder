package org.xssfinder.runner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.remote.PageDefinition;
import org.xssfinder.reporting.RouteRunErrorContext;
import org.xssfinder.reporting.RouteRunErrorContextFactory;
import org.xssfinder.routing.Route;
import org.xssfinder.reporting.XssJournal;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RoutePageStrategyRunnerTest {
    private static final String URL = "http://localhost";

    @Mock
    private ExecutorWrapper mockExecutor;
    @Mock
    private PageContextFactory mockPageContextFactory;
    @Mock
    private PageContext mockPageContext;
    @Mock
    private Route mockRoute;
    @Mock
    private XssJournal mockXssJournal;
    @Mock
    private Object mockLifecycleHandler;
    @Mock
    private LifecycleEventExecutor mockLifecycleEventExecutor;
    @Mock
    private RouteRunErrorContextFactory mockErrorContextFactory;
    @Mock
    private PageDefinition mockPageDefinition;

    private final List<Route> routes = new ArrayList<Route>();
    private final List<PageStrategy> pageStrategies = new ArrayList<PageStrategy>();

    private RoutePageStrategyRunner runner;

    @Before
    public void setUp() throws Exception {
        when(mockPageContextFactory.createContext(mockExecutor, mockRoute, mockXssJournal)).thenReturn(mockPageContext);
        when(mockPageContext.getPageDefinition()).thenReturn(mockPageDefinition);
        when(mockRoute.getUrl()).thenReturn(URL);
        when(mockRoute.createLifecycleHandler()).thenReturn(mockLifecycleHandler);
        routes.add(mockRoute);

        runner = new RoutePageStrategyRunner(
                mockExecutor,
                mockPageContextFactory,
                mockLifecycleEventExecutor,
                mockErrorContextFactory
        );
    }

    @Test
    public void runnerOpensDriverWrapperAtStartPointOfRoute() {
        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockExecutor).visit(URL);
    }

    @Test
    public void noTraversalsTakenIfNoNextContext() throws Exception {
        //
        when(mockPageContext.hasNextContext()).thenReturn(false);

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockPageContext, never()).getNextContext();
    }

    @Test
    public void firstAndOnlyContextIsProcessedByStrategiesAndThenAfterRouteIsCalled() {
        // given
        PageStrategy mockStrategy1 = mock(PageStrategy.class);
        PageStrategy mockStrategy2 = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy1);
        pageStrategies.add(mockStrategy2);

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        InOrder inOrder = inOrder(mockStrategy1, mockStrategy2, mockLifecycleEventExecutor);
        inOrder.verify(mockStrategy1).processPage(mockPageContext, mockXssJournal);
        inOrder.verify(mockStrategy2).processPage(mockPageContext, mockXssJournal);
        inOrder.verify(mockLifecycleEventExecutor).afterRoute(mockLifecycleHandler, mockPageDefinition);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void secondContextIsProcessedByStrategiesAndThenAfterRouteIsCalled() {
        // given
        PageStrategy mockStrategy1 = mock(PageStrategy.class);
        PageStrategy mockStrategy2 = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy1);
        pageStrategies.add(mockStrategy2);
        PageContext mockNextContext = mock(PageContext.class);
        when(mockPageContext.hasNextContext()).thenReturn(true);
        when(mockPageContext.getNextContext()).thenReturn(mockNextContext);
        PageDefinition mockNextPage = mock(PageDefinition.class);
        when(mockNextContext.getPageDefinition()).thenReturn(mockNextPage);

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        InOrder inOrder = inOrder(mockStrategy1, mockStrategy2, mockLifecycleEventExecutor);
        inOrder.verify(mockStrategy1).processPage(mockPageContext, mockXssJournal);
        inOrder.verify(mockStrategy2).processPage(mockPageContext, mockXssJournal);
        inOrder.verify(mockStrategy1).processPage(mockNextContext, mockXssJournal);
        inOrder.verify(mockStrategy2).processPage(mockNextContext, mockXssJournal);
        inOrder.verify(mockLifecycleEventExecutor).afterRoute(mockLifecycleHandler, mockNextPage);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void exceptionThrownByTraversingDoesNotPreventOtherRoutesFromExecuting() {
        // given
        PageStrategy mockStrategy = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy);
        Route mockOtherRoute = mock(Route.class);
        PageContext mockOtherPageContext = mock(PageContext.class);
        when(mockPageContextFactory.createContext(mockExecutor, mockOtherRoute, mockXssJournal)).thenReturn(mockOtherPageContext);
        when(mockOtherPageContext.getPageDefinition()).thenReturn(mockPageDefinition);
        when(mockOtherRoute.getUrl()).thenReturn(URL);
        when(mockOtherRoute.createLifecycleHandler()).thenReturn(mockLifecycleHandler);
        routes.add(mockOtherRoute);
        when(mockPageContext.hasNextContext()).thenReturn(true, false);
        when(mockPageContext.getNextContext()).thenThrow(new RuntimeException("Error!"));

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockStrategy).processPage(mockOtherPageContext, mockXssJournal);
    }

    @Test
    public void exceptionThrownByTraversingDoesNotPreventAfterRouteFromBeingHandled() {
        // given
        PageStrategy mockStrategy = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy);
        when(mockPageContext.hasNextContext()).thenReturn(true, false);
        when(mockPageContext.getNextContext()).thenThrow(new RuntimeException("Error!"));

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockLifecycleEventExecutor).afterRoute(mockLifecycleHandler, mockPageDefinition);
    }

    @Test
    public void exceptionThrowByTraversingIsLoggedInXssJournal() {
        // given
        PageStrategy mockStrategy = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy);
        when(mockPageContext.hasNextContext()).thenReturn(true, false);
        Exception runtimeException = new RuntimeException("Error!");
        when(mockPageContext.getNextContext()).thenThrow(runtimeException);
        RouteRunErrorContext mockErrorContext =  mock(RouteRunErrorContext.class);
        when(mockErrorContextFactory.createErrorContext(runtimeException, mockPageContext))
                .thenReturn(mockErrorContext);

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockXssJournal).addErrorContext(mockErrorContext);
    }

    @Test
    public void exceptionThrownByAfterRouteDoesNotPreventOtherRoutesFromBeingExecuted() {
        // given
        PageStrategy mockStrategy = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy);
        Route mockOtherRoute = mock(Route.class);
        PageContext mockOtherPageContext = mock(PageContext.class);
        when(mockPageContextFactory.createContext(mockExecutor, mockOtherRoute, mockXssJournal)).thenReturn(mockOtherPageContext);
        when(mockOtherPageContext.getPageDefinition()).thenReturn(mockPageDefinition);
        when(mockOtherRoute.getUrl()).thenReturn(URL);
        when(mockOtherRoute.createLifecycleHandler()).thenReturn(mockLifecycleHandler);
        routes.add(mockOtherRoute);

        doThrow(new RuntimeException("Error!"))
                .when(mockLifecycleEventExecutor).afterRoute(mockLifecycleHandler, mockPageDefinition);

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockStrategy).processPage(mockOtherPageContext, mockXssJournal);
    }

    @Test
    public void afterRouteNotCalledIfExceptionThrownBeforePageContextCanBeCreated() {
        // given
        when(mockPageContextFactory.createContext(mockExecutor, mockRoute, mockXssJournal))
                .thenThrow(new RuntimeException("Error!"));

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verifyZeroInteractions(mockLifecycleEventExecutor);
    }
}
