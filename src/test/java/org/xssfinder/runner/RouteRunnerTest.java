package org.xssfinder.runner;

import org.dummytest.simple.HomePage;
import org.dummytest.simple.SecondPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.routing.Route;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class RouteRunnerTest {
    private static final String URL = "http://localhost";

    @Mock
    private DriverWrapper mockDriverWrapper;
    @Mock
    private PageInstantiator mockPageInstantiator;
    @Mock
    private PageTraverser mockPageTraverser;
    @Mock
    private Route route;

    private List<Route> routes = new ArrayList<Route>();

    @Before
    public void setUp() {
        when(route.getUrl()).thenReturn(URL);
        routes.add(route);
    }

    @Test
    public void runnerOpensWebDriverAtStartPointOfRoute() {
        // given
        RouteRunner runner = new RouteRunner(mockDriverWrapper, mockPageInstantiator, mockPageTraverser, routes);

        // when
        runner.run();

        // then
        verify(mockDriverWrapper).visit(URL);
    }

    @Test
    public void runnerInstantiatesPagesInRoute() throws Exception {
        // given
        setRootPageToHomePage();
        RouteRunner runner = new RouteRunner(mockDriverWrapper, mockPageInstantiator, mockPageTraverser, routes);

        // when
        runner.run();

        // then
        verify(mockPageInstantiator).instantiatePage(HomePage.class);
    }

    @Test
    public void noTraversalsTakenForSinglePageRoute() throws Exception {
        // given
        RouteRunner runner = new RouteRunner(mockDriverWrapper, mockPageInstantiator, mockPageTraverser, routes);

        // when
        runner.run();

        // then
        verifyZeroInteractions(mockPageTraverser);
    }

    @Test
    public void traversalIsTakenForTwoPageRoute() throws Exception {
        // given
        setRootPageToHomePage();
        HomePage mockHomePage = setUpInstantiationOfHomePage();
        PageTraversal mockPageTraversal = addTraversalToRoute();
        RouteRunner runner = new RouteRunner(mockDriverWrapper, mockPageInstantiator, mockPageTraverser, routes);

        // when
        runner.run();

        // then
        verify(mockPageTraverser).traverse(mockHomePage, mockPageTraversal);
    }

    @Test
    public void traversalIsTakenOnPageFromPreviousTraversal() throws Exception {
        // given
        setRootPageToHomePage();
        HomePage mockHomePage = setUpInstantiationOfHomePage();
        SecondPage mockSecondPage1 = setUpTraversalOfTraversal(mockHomePage, addTraversalToRoute());
        PageTraversal mockPageTraversal = addTraversal(route.getPageTraversal());
        RouteRunner runner = new RouteRunner(mockDriverWrapper, mockPageInstantiator, mockPageTraverser, routes);

        // when
        runner.run();

        // then
        verify(mockPageTraverser).traverse(mockSecondPage1, mockPageTraversal);
    }

    private void setRootPageToHomePage() {
        when(route.getRootPageClass()).thenReturn((Class)HomePage.class);
    }

    private HomePage setUpInstantiationOfHomePage() {
        HomePage mockHomePage = mock(HomePage.class);
        when(mockPageInstantiator.instantiatePage(HomePage.class)).thenReturn(mockHomePage);
        return mockHomePage;
    }

    private PageTraversal addTraversalToRoute() throws Exception {
        PageTraversal mockPageTraversal = mock(PageTraversal.class);
        when(mockPageTraversal.getMethod()).thenReturn(HomePage.class.getMethod("goToSecondPage"));
        when(route.getPageTraversal()).thenReturn(mockPageTraversal);
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
