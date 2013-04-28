package org.xssfinder.runner;

import org.dummytest.simple.HomePage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.routing.Route;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouteRunnerTest {
    private static final String URL = "http://localhost";

    @Mock
    private DriverWrapper mockDriverWrapper;
    @Mock
    private PageInstantiator mockPageInstantiator;
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
        RouteRunner runner = new RouteRunner(mockDriverWrapper, mockPageInstantiator, routes);

        // when
        runner.run();

        // then
        verify(mockDriverWrapper).visit(URL);
    }

    @Test
    public void runnerInstantiatesPagesInRoute() throws Exception {
        // given
        PageTraversal mockPageTraversal = mock(PageTraversal.class);
        when(route.getRootPageClass()).thenReturn((Class)HomePage.class);
        when(mockPageTraversal.getMethod()).thenReturn(HomePage.class.getMethod("goToSecondPage"));
        RouteRunner runner = new RouteRunner(mockDriverWrapper, mockPageInstantiator, routes);

        // when
        runner.run();

        // then
        verify(mockPageInstantiator).instantiatePage(HomePage.class);
    }
}
