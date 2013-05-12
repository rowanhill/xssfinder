package org.xssfinder.runner;

import com.google.common.collect.ImmutableSet;
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
import org.xssfinder.xss.XssDetector;
import org.xssfinder.xss.XssGenerator;
import org.xssfinder.xss.XssJournal;

import java.util.*;

import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class RouteRunnerTest {
    private static final String URL = "http://localhost";

    @Mock
    private PageAttacker mockPageAttacker;
    @Mock
    private XssDetector mockXssDetector;
    @Mock
    private DriverWrapper mockDriverWrapper;
    @Mock
    private PageInstantiator mockPageInstantiator;
    @Mock
    private PageTraverser mockPageTraverser;
    @Mock
    private XssGenerator mockXssGenerator;
    @Mock
    private XssJournal mockXssJournal;
    @Mock
    private Route route;

    private List<Route> routes = new ArrayList<Route>();

    private RouteRunner runner;

    @Before
    public void setUp() {
        when(mockDriverWrapper.getPageInstantiator()).thenReturn(mockPageInstantiator);
        when(route.getUrl()).thenReturn(URL);
        routes.add(route);

        runner = new RouteRunner(mockPageAttacker, mockXssDetector, mockDriverWrapper, mockPageTraverser, mockXssJournal, routes);
    }

    @Test
    public void runnerOpensWebDriverAtStartPointOfRoute() {
        // when
        runner.run();

        // then
        verify(mockDriverWrapper).visit(URL);
    }

    @Test
    public void runnerInstantiatesPagesInRoute() throws Exception {
        // given
        routeStartsAtHomePage();

        // when
        runner.run();

        // then
        verify(mockPageInstantiator).instantiatePage(HomePage.class);
    }

    @Test
    public void noTraversalsTakenForSinglePageRoute() throws Exception {
        // when
        runner.run();

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
        runner.run();

        // then
        verify(mockPageTraverser).traverse(mockHomePage, mockPageTraversal);
    }

    @Test
    public void traversalIsTakenOnPageFromPreviousTraversal() throws Exception {
        // given
        routeStartsAtHomePage();
        HomePage mockHomePage = instantiatorReturnsMockHomePage();
        SecondPage mockSecondPage1 = setUpTraversalOfTraversal(mockHomePage, addTraversalToRoute());
        PageTraversal mockPageTraversal = addTraversal(route.getPageTraversal());

        // when
        runner.run();

        // then
        verify(mockPageTraverser).traverse(mockSecondPage1, mockPageTraversal);
    }

    @Test
    public void allButFinalPageIsAttacked() throws Exception {
        // given
        routeStartsAtHomePage();
        HomePage mockHomePage = instantiatorReturnsMockHomePage();
        PageTraversal mockPageTraversal1 = addTraversalToRoute();
        SecondPage mockSecondPage = setUpTraversalOfTraversal(mockHomePage, mockPageTraversal1);
        PageTraversal mockPageTraversal2 = addTraversal(mockPageTraversal1);

        // when
        runner.run();

        // then
        verify(mockPageAttacker).attackIfAboutToSubmit(mockHomePage, mockDriverWrapper, mockPageTraversal1);
        verify(mockPageAttacker).attackIfAboutToSubmit(mockSecondPage, mockDriverWrapper, mockPageTraversal2);
    }

    @Test
    public void xssFoundOnPageIsMarkedAsSuccessful() throws Exception {
        // given
        routeStartsAtHomePage();
        HomePage mockHomePage = instantiatorReturnsMockHomePage();
        PageTraversal mockPageTraversal = addTraversalToRoute();
        setUpTraversalOfTraversal(mockHomePage, mockPageTraversal);
        Set<String> homePageXssIdentifiers = Collections.emptySet();
        Set<String> secondPageXssIdentifiers = ImmutableSet.of("1");
        when(mockXssDetector.getCurrentXssIds(mockDriverWrapper)).thenReturn(
                homePageXssIdentifiers,
                secondPageXssIdentifiers
        );

        // when
        runner.run();

        // then
        InOrder inOrder = inOrder(mockXssJournal);
        inOrder.verify(mockXssJournal).markAsSuccessful(homePageXssIdentifiers);
        inOrder.verify(mockXssJournal).markAsSuccessful(secondPageXssIdentifiers);
        inOrder.verifyNoMoreInteractions();
    }

    private void routeStartsAtHomePage() {
        when(route.getRootPageClass()).thenReturn((Class)HomePage.class);
    }

    private HomePage instantiatorReturnsMockHomePage() {
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
