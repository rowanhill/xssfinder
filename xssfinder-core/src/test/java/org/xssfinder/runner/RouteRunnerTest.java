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
import org.xssfinder.xss.XssGenerator;

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
    private XssGenerator mockXssGenerator;
    @Mock
    private Route route;

    private List<Route> routes = new ArrayList<Route>();

    private RouteRunner runner;

    @Before
    public void setUp() {
        when(mockDriverWrapper.getPageInstantiator()).thenReturn(mockPageInstantiator);
        when(route.getUrl()).thenReturn(URL);
        routes.add(route);

        runner = new RouteRunner(mockDriverWrapper, mockPageTraverser, mockXssGenerator, routes);
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
        setRootPageToHomePage();

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
        setRootPageToHomePage();
        HomePage mockHomePage = setUpInstantiationOfHomePage();
        PageTraversal mockPageTraversal = addTraversalToRoute();

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

        // when
        runner.run();

        // then
        verify(mockPageTraverser).traverse(mockSecondPage1, mockPageTraversal);
    }

    @Test
    public void inputsAreFilledWithXssAttacksPriorToSubmitActionTraversal() throws Exception {
        // given
        setRootPageToHomePage();
        HomePage mockHomePage = setUpInstantiationOfHomePage();
        PageTraversal mockPageTraversal1 = addTraversalToRoute();
        SecondPage mockSecondPage = setUpTraversalOfTraversal(mockHomePage, mockPageTraversal1);
        PageTraversal mockPageTraversal2 = addTraversal(mockPageTraversal1);
        when(mockPageTraversal2.isSubmit()).thenReturn(true);

        // when
        runner.run();

        // then
        InOrder inOrder = inOrder(mockPageTraverser, mockDriverWrapper);
        inOrder.verify(mockPageTraverser).traverse(mockHomePage, mockPageTraversal1);
        inOrder.verify(mockDriverWrapper).putXssAttackStringsInInputs(mockXssGenerator);
        inOrder.verify(mockPageTraverser).traverse(mockSecondPage, mockPageTraversal2);
        inOrder.verifyNoMoreInteractions();
        verify(mockDriverWrapper, times(1)).putXssAttackStringsInInputs(mockXssGenerator);
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
