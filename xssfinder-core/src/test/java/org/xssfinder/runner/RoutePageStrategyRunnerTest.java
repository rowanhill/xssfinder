package org.xssfinder.runner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.remote.PageDefinition;
import org.xssfinder.remote.TWebInteractionException;
import org.xssfinder.reporting.RouteRunErrorContext;
import org.xssfinder.reporting.RouteRunErrorContextFactory;
import org.xssfinder.routing.Route;
import org.xssfinder.reporting.XssJournal;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RoutePageStrategyRunnerTest {
    private static final String PAGE_ID = "some page";

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
    private RouteRunErrorContextFactory mockErrorContextFactory;
    @Mock
    private PageDefinition mockPageDefinition;

    private final List<Route> routes = new ArrayList<Route>();
    private final List<PageStrategy> pageStrategies = new ArrayList<PageStrategy>();

    private RoutePageStrategyRunner runner;

    @Before
    public void setUp() throws Exception {
        when(mockPageContextFactory.createContext(mockRoute)).thenReturn(mockPageContext);
        when(mockPageContext.getPageDefinition()).thenReturn(mockPageDefinition);
        when(mockRoute.getRootPageDefinition()).thenReturn(mockPageDefinition);
        when(mockPageDefinition.getIdentifier()).thenReturn(PAGE_ID);
        routes.add(mockRoute);

        runner = new RoutePageStrategyRunner(
                mockExecutor,
                mockPageContextFactory,
                mockErrorContextFactory
        );
    }

    @Test
    public void runnerOpensDriverWrapperAtStartPointOfRoute() throws Exception {
        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockExecutor).startRoute(PAGE_ID);
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
    public void firstAndOnlyContextIsProcessedByStrategiesAndThenAfterRouteIsCalled() throws Exception {
        // given
        PageStrategy mockStrategy1 = mock(PageStrategy.class);
        PageStrategy mockStrategy2 = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy1);
        pageStrategies.add(mockStrategy2);

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        InOrder inOrder = inOrder(mockStrategy1, mockStrategy2, mockExecutor);
        inOrder.verify(mockStrategy1).processPage(mockPageContext, mockXssJournal);
        inOrder.verify(mockStrategy2).processPage(mockPageContext, mockXssJournal);
        inOrder.verify(mockExecutor).invokeAfterRouteHandler(PAGE_ID);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void secondContextIsProcessedByStrategiesAndThenAfterRouteIsCalled() throws Exception {
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
        InOrder inOrder = inOrder(mockStrategy1, mockStrategy2, mockExecutor);
        inOrder.verify(mockStrategy1).processPage(mockPageContext, mockXssJournal);
        inOrder.verify(mockStrategy2).processPage(mockPageContext, mockXssJournal);
        inOrder.verify(mockStrategy1).processPage(mockNextContext, mockXssJournal);
        inOrder.verify(mockStrategy2).processPage(mockNextContext, mockXssJournal);
        inOrder.verify(mockExecutor).invokeAfterRouteHandler(PAGE_ID);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void exceptionThrownByStartingRouteDoesNotPreventOtherRoutesFromExecuting() throws Exception {
        // given
        PageStrategy mockStrategy = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy);
        Route mockOtherRoute = mock(Route.class);
        PageContext mockOtherPageContext = mock(PageContext.class);
        when(mockPageContextFactory.createContext(mockOtherRoute)).thenReturn(mockOtherPageContext);
        PageDefinition mockOtherPageDefinition = mock(PageDefinition.class);
        when(mockOtherPageContext.getPageDefinition()).thenReturn(mockOtherPageDefinition);
        when(mockOtherRoute.getRootPageDefinition()).thenReturn(mockOtherPageDefinition);
        routes.add(mockOtherRoute);
        doThrow(new TWebInteractionException("Error!")).when(mockExecutor).startRoute(PAGE_ID);

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockStrategy).processPage(mockOtherPageContext, mockXssJournal);
    }

    @Test
    public void exceptionThrownByStartingRouteIsLoggedInXssJournal() throws Exception {
        // given
        PageStrategy mockStrategy = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy);
        TWebInteractionException webInteractionException = new TWebInteractionException("Error!");
        doThrow(webInteractionException).when(mockExecutor).startRoute(PAGE_ID);
        RouteRunErrorContext mockErrorContext =  mock(RouteRunErrorContext.class);
        when(mockErrorContextFactory.createErrorContext(webInteractionException, null))
                .thenReturn(mockErrorContext);

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockXssJournal).addErrorContext(mockErrorContext);
    }

    @Test
    public void exceptionThrownByProcessingPageDoesNotPreventOtherRoutesFromExecuting() throws Exception {
        // given
        PageStrategy mockStrategy = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy);
        Route mockOtherRoute = mock(Route.class);
        PageContext mockOtherPageContext = mock(PageContext.class);
        when(mockPageContextFactory.createContext(mockOtherRoute)).thenReturn(mockOtherPageContext);
        when(mockOtherPageContext.getPageDefinition()).thenReturn(mockPageDefinition);
        when(mockOtherRoute.getRootPageDefinition()).thenReturn(mockPageDefinition);
        routes.add(mockOtherRoute);
        doThrow(new TWebInteractionException("Error!")).when(mockStrategy).processPage(mockPageContext, mockXssJournal);

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockStrategy).processPage(mockOtherPageContext, mockXssJournal);
    }

    @Test
    public void exceptionThrownByProcessingPageIsLoggedInXssJournal() throws Exception {
        // given
        PageStrategy mockStrategy = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy);
        TWebInteractionException webInteractionException = new TWebInteractionException("Error!");
        doThrow(webInteractionException).when(mockStrategy).processPage(mockPageContext, mockXssJournal);
        RouteRunErrorContext mockErrorContext =  mock(RouteRunErrorContext.class);
        when(mockErrorContextFactory.createErrorContext(webInteractionException, mockPageContext))
                .thenReturn(mockErrorContext);

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockXssJournal).addErrorContext(mockErrorContext);
    }

    @Test
    public void exceptionThrownByTraversingDoesNotPreventOtherRoutesFromExecuting() throws Exception {
        // given
        PageStrategy mockStrategy = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy);
        Route mockOtherRoute = mock(Route.class);
        PageContext mockOtherPageContext = mock(PageContext.class);
        when(mockPageContextFactory.createContext(mockOtherRoute)).thenReturn(mockOtherPageContext);
        when(mockOtherPageContext.getPageDefinition()).thenReturn(mockPageDefinition);
        when(mockOtherRoute.getRootPageDefinition()).thenReturn(mockPageDefinition);
        routes.add(mockOtherRoute);
        when(mockPageContext.hasNextContext()).thenReturn(true, false);
        when(mockPageContext.getNextContext()).thenThrow(new RuntimeException("Error!"));

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockStrategy).processPage(mockOtherPageContext, mockXssJournal);
    }

    @Test
    public void exceptionThrownByTraversingDoesNotPreventAfterRouteFromBeingHandled() throws Exception {
        // given
        PageStrategy mockStrategy = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy);
        when(mockPageContext.hasNextContext()).thenReturn(true, false);
        when(mockPageContext.getNextContext()).thenThrow(new TWebInteractionException("Error!"));

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockExecutor).invokeAfterRouteHandler(PAGE_ID);
    }

    @Test
    public void exceptionThrowByTraversingIsLoggedInXssJournal() throws Exception {
        // given
        PageStrategy mockStrategy = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy);
        when(mockPageContext.hasNextContext()).thenReturn(true, false);
        TWebInteractionException webInteractionException = new TWebInteractionException("Error!");
        when(mockPageContext.getNextContext()).thenThrow(webInteractionException);
        RouteRunErrorContext mockErrorContext =  mock(RouteRunErrorContext.class);
        when(mockErrorContextFactory.createErrorContext(webInteractionException, mockPageContext))
                .thenReturn(mockErrorContext);

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockXssJournal).addErrorContext(mockErrorContext);
    }

    @Test
    public void exceptionThrownByAfterRouteDoesNotPreventOtherRoutesFromBeingExecuted() throws Exception {
        // given
        PageStrategy mockStrategy = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy);
        Route mockOtherRoute = mock(Route.class);
        PageContext mockOtherPageContext = mock(PageContext.class);
        when(mockPageContextFactory.createContext(mockOtherRoute)).thenReturn(mockOtherPageContext);
        when(mockOtherPageContext.getPageDefinition()).thenReturn(mockPageDefinition);
        when(mockOtherRoute.getRootPageDefinition()).thenReturn(mockPageDefinition);
        routes.add(mockOtherRoute);

        doThrow(new TWebInteractionException("Error!")).when(mockExecutor).invokeAfterRouteHandler(PAGE_ID);

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockStrategy).processPage(mockOtherPageContext, mockXssJournal);
    }

    @Test
    public void exceptionThrownByAfterRouteIsLoggedInXssJournal() throws Exception {
        // given
        PageStrategy mockStrategy = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy);
        TWebInteractionException webInteractionException = new TWebInteractionException("Error!");
        doThrow(webInteractionException).when(mockExecutor).invokeAfterRouteHandler(PAGE_ID);
        RouteRunErrorContext mockErrorContext =  mock(RouteRunErrorContext.class);
        when(mockErrorContextFactory.createErrorContext(webInteractionException, mockPageContext))
                .thenReturn(mockErrorContext);

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockXssJournal).addErrorContext(mockErrorContext);
    }

    @Test
    public void afterRouteNotCalledIfExceptionThrownBeforePageContextCanBeCreated() throws Exception {
        // given
        when(mockPageContextFactory.createContext(mockRoute))
                .thenThrow(new RuntimeException("Error!"));

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockExecutor, never()).invokeAfterRouteHandler(PAGE_ID);
    }
}
