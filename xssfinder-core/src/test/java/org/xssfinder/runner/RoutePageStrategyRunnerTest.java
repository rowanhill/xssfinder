package org.xssfinder.runner;

import org.dummytest.simple.HomePage;
import org.dummytest.simple.SecondPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.routing.Route;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class RoutePageStrategyRunnerTest {
    private static final String URL = "http://localhost";

    @Mock
    private DriverWrapper mockDriverWrapper;
    @Mock
    private PageInstantiator mockPageInstantiator;
    @Mock
    private PageTraverser mockPageTraverser;
    @Mock
    private Route mockRoute;

    private final List<Route> routes = new ArrayList<Route>();
    private final List<PageStrategy> pageStrategies = new ArrayList<PageStrategy>();

    private RoutePageStrategyRunner runner;

    @Before
    public void setUp() {
        when(mockDriverWrapper.getPageInstantiator()).thenReturn(mockPageInstantiator);
        when(mockRoute.getUrl()).thenReturn(URL);
        routes.add(mockRoute);

        runner = new RoutePageStrategyRunner(mockDriverWrapper, mockPageInstantiator, mockPageTraverser);
    }

    @Test
    public void runnerOpensDriverWrapperAtStartPointOfRoute() {
        // when
        runner.run(routes, pageStrategies);

        // then
        verify(mockDriverWrapper).visit(URL);
    }

    @Test
    public void runnerInstantiatesRouteHomePage() throws Exception {
        // given
        routeStartsAtHomePage();

        // when
        runner.run(routes, pageStrategies);

        // then
        verify(mockPageInstantiator).instantiatePage(HomePage.class);
    }

    @Test
    public void noTraversalsTakenForSinglePageRoute() throws Exception {
        // when
        runner.run(routes, pageStrategies);

        // then
        verifyZeroInteractions(mockPageTraverser);
    }

    @Test
    public void traversalIsTakenForTwoPageRoute() throws Exception {
        // given
        routeStartsAtHomePage();
        HomePage mockHomePage = instantiatorReturnsMockHomePage();
        PageTraversal mockPageTraversal = addTraversalToRoute();

        // when
        runner.run(routes, pageStrategies);

        // then
        verify(mockPageTraverser).traverse(mockHomePage, mockPageTraversal);
    }

    @Test
    public void traversalIsTakenOnPageFromPreviousTraversal() throws Exception {
        // given
        routeStartsAtHomePage();
        HomePage mockHomePage = instantiatorReturnsMockHomePage();
        SecondPage mockSecondPage = setUpTraversalOfTraversal(mockHomePage, addTraversalToRoute());
        PageTraversal mockPageTraversal = addTraversal(mockRoute.getPageTraversal());

        // when
        runner.run(routes, pageStrategies);

        // then
        verify(mockPageTraverser).traverse(mockSecondPage, mockPageTraversal);
    }

    @Test
    public void pageStrategiesAreRunOnEachPageInRoute() throws Exception {
        // given
        routeStartsAtHomePage();
        HomePage mockHomePage = instantiatorReturnsMockHomePage();
        PageTraversal mockPageTraversal = addTraversalToRoute();
        SecondPage mockSecondPage = setUpTraversalOfTraversal(mockHomePage, mockPageTraversal);
        PageStrategy mockStrategy1 = mock(PageStrategy.class);
        PageStrategy mockStrategy2 = mock(PageStrategy.class);
        pageStrategies.add(mockStrategy1);
        pageStrategies.add(mockStrategy2);

        // when
        runner.run(routes, pageStrategies);

        // then
        InOrder inOrder = inOrder(mockStrategy1, mockStrategy2);
        inOrder.verify(mockStrategy1).processPage(mockHomePage, mockPageTraversal, mockDriverWrapper);
        inOrder.verify(mockStrategy2).processPage(mockHomePage, mockPageTraversal, mockDriverWrapper);
        inOrder.verify(mockStrategy1).processPage(mockSecondPage, null, mockDriverWrapper);
        inOrder.verify(mockStrategy2).processPage(mockSecondPage, null, mockDriverWrapper);
        inOrder.verifyNoMoreInteractions();
    }

    @SuppressWarnings("unchecked")
    private void routeStartsAtHomePage() {
        when(mockRoute.getRootPageClass()).thenReturn((Class)HomePage.class);
    }

    private HomePage instantiatorReturnsMockHomePage() {
        HomePage mockHomePage = mock(HomePage.class);
        when(mockPageInstantiator.instantiatePage(HomePage.class)).thenReturn(mockHomePage);
        return mockHomePage;
    }

    private PageTraversal addTraversalToRoute() throws Exception {
        PageTraversal mockPageTraversal = mock(PageTraversal.class);
        when(mockPageTraversal.getMethod()).thenReturn(HomePage.class.getMethod("goToSecondPage"));
        when(mockRoute.getPageTraversal()).thenReturn(mockPageTraversal);
        return mockPageTraversal;
    }

    private PageTraversal addTraversal(PageTraversal mockPageTraversal) {
        PageTraversal mockPageTraversal2 = mock(PageTraversal.class);
        when(mockPageTraversal.getNextTraversal()).thenReturn(mockPageTraversal2);
        return mockPageTraversal2;
    }

    private SecondPage setUpTraversalOfTraversal(Object page, PageTraversal traversal) {
        SecondPage mockSecondPage = mock(SecondPage.class);
        when(mockPageTraverser.traverse(page, traversal)).thenReturn(mockSecondPage);
        return mockSecondPage;
    }
}
