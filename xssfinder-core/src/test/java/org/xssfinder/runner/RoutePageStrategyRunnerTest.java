package org.xssfinder.runner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.routing.Route;
import org.xssfinder.reporting.XssJournal;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RoutePageStrategyRunnerTest {
    private static final String URL = "http://localhost";

    @Mock
    private DriverWrapper mockDriverWrapper;
    @Mock
    private PageContextFactory mockContextFactory;
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

    private final List<Route> routes = new ArrayList<Route>();
    private final List<PageStrategy> pageStrategies = new ArrayList<PageStrategy>();

    private RoutePageStrategyRunner runner;

    @Before
    public void setUp() throws Exception {
        when(mockContextFactory.createContext(mockDriverWrapper, mockRoute)).thenReturn(mockPageContext);
        when(mockRoute.getUrl()).thenReturn(URL);
        when(mockRoute.createLifecycleHandler()).thenReturn(mockLifecycleHandler);
        routes.add(mockRoute);

        runner = new RoutePageStrategyRunner(mockDriverWrapper, mockContextFactory, mockLifecycleEventExecutor);
    }

    @Test
    public void runnerOpensDriverWrapperAtStartPointOfRoute() {
        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        verify(mockDriverWrapper).visit(URL);
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
        inOrder.verify(mockLifecycleEventExecutor).afterRoute(mockLifecycleHandler, mockPageContext);
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

        // when
        runner.run(routes, pageStrategies, mockXssJournal);

        // then
        InOrder inOrder = inOrder(mockStrategy1, mockStrategy2, mockLifecycleEventExecutor);
        inOrder.verify(mockStrategy1).processPage(mockPageContext, mockXssJournal);
        inOrder.verify(mockStrategy2).processPage(mockPageContext, mockXssJournal);
        inOrder.verify(mockStrategy1).processPage(mockNextContext, mockXssJournal);
        inOrder.verify(mockStrategy2).processPage(mockNextContext, mockXssJournal);
        inOrder.verify(mockLifecycleEventExecutor).afterRoute(mockLifecycleHandler, mockNextContext);
        inOrder.verifyNoMoreInteractions();
    }
}
