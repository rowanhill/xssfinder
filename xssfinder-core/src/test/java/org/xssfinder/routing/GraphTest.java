package org.xssfinder.routing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;
import org.xssfinder.SubmitAction;
import org.xssfinder.reflection.Instantiator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GraphTest {
    @Mock
    private Instantiator mockInstantiator;

    private PageDescriptor ordinaryPageDescriptor;
    private PageDescriptor startPage1Descriptor;
    private PageDescriptor startPage2Descriptor;
    private PageDescriptor forkStartPageDescriptor;
    private PageDescriptor forkChildPageOneDescriptor;
    private PageDescriptor forkChildPageTwoDescriptor;

    private final Set<PageDescriptor> pagesDescriptors = new HashSet<PageDescriptor>();

    @Before
    public void setup() {
        ordinaryPageDescriptor = new PageDescriptor(OrdinaryPage.class);
        startPage1Descriptor = new PageDescriptor(StartPageOne.class);
        startPage2Descriptor = new PageDescriptor(StartPageTwo.class);
        forkStartPageDescriptor = new PageDescriptor(ForkStartPage.class);
        forkChildPageOneDescriptor = new PageDescriptor(ForkChildPageOne.class);
        forkChildPageTwoDescriptor = new PageDescriptor(ForkChildPageTwo.class);
    }

    @Test(expected=NoRootPageFoundException.class)
    public void constructorThrowsExceptionIfNoRootNodeIsFound() {
        // given
        pagesDescriptors.add(ordinaryPageDescriptor);

        // when
        new Graph(pagesDescriptors, mockInstantiator);
    }

    @Test(expected=MultipleRootPagesFoundException.class)
    public void constructorThrowsExceptionIfMultipleRootNodesAreFound() {
        // given
        pagesDescriptors.add(startPage1Descriptor);
        pagesDescriptors.add(startPage2Descriptor);

        // when
        new Graph(pagesDescriptors, mockInstantiator);
    }

    @Test
    public void simpleLinearlyRelatedPagesReturnsOneRoute() {
        // given
        pagesDescriptors.add(startPage1Descriptor);
        pagesDescriptors.add(ordinaryPageDescriptor);
        Graph graph = new Graph(pagesDescriptors, mockInstantiator);

        // when
        List<Route> routes = graph.getRoutes();

        // then
        assertThat(routes.size(), is(1));
    }

    @Test
    public void simpleLinearlyRelatedPagesRouteHasOneTraversal() {
        // given
        pagesDescriptors.add(startPage1Descriptor);
        pagesDescriptors.add(ordinaryPageDescriptor);
        Graph graph = new Graph(pagesDescriptors, mockInstantiator);

        // when
        List<Route> routes = graph.getRoutes();

        // then
        Route route = routes.get(0);
        Class<?> pageClass = route.getRootPageClass();
        assertThat(pageClass == StartPageOne.class, is(true));

        PageTraversal traversal = route.getPageTraversal();
        pageClass = traversal.getMethod().getReturnType();
        assertThat(pageClass == OrdinaryPage.class, is(true));

        traversal = traversal.getNextTraversal();
        assertThat(traversal, is(nullValue()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void forkingPagesReturnTwoRoutes() {
        // given
        pagesDescriptors.add(forkStartPageDescriptor);
        pagesDescriptors.add(forkChildPageOneDescriptor);
        pagesDescriptors.add(forkChildPageTwoDescriptor);
        Graph graph = new Graph(pagesDescriptors, mockInstantiator);

        // when
        List<Route> routes = graph.getRoutes();

        // then
        assertThat(routes.size(), is(2));
    }

    @Test
    public void submitActionThatWouldNotFormPartOfMinimalRoutesIsAppended() {
        // given
        pagesDescriptors.add(new PageDescriptor(LoginPage.class));
        pagesDescriptors.add(new PageDescriptor(SignUpPage.class));
        Graph graph = new Graph(pagesDescriptors, mockInstantiator);

        // when
        List<Route> routes = graph.getRoutes();

        // then
        assertThat(routes.size(), is(1));
        Route route = routes.get(0);
        Class<?> pageClass = route.getRootPageClass();
        assertThat(pageClass == LoginPage.class, is(true));

        PageTraversal traversal = route.getPageTraversal();
        pageClass = traversal.getMethod().getReturnType();
        assertThat(pageClass == SignUpPage.class, is(true));

        traversal = traversal.getNextTraversal();
        pageClass = traversal.getMethod().getReturnType();
        assertThat(pageClass == LoginPage.class, is(true));

        traversal = traversal.getNextTraversal();
        assertThat(traversal, is(nullValue()));
    }

    @Test
    public void singleCircularPageProducesSingleRouteTraversingToSamePage() {
        // given
        pagesDescriptors.add(new PageDescriptor(CircularHomePage.class));
        Graph graph = new Graph(pagesDescriptors, mockInstantiator);

        // when
        List<Route> routes = graph.getRoutes();

        // then
        assertThat(routes.size(), is(1));
        Route route = routes.get(0);
        Class<?> pageClass = route.getRootPageClass();
        assertThat(pageClass == CircularHomePage.class, is(true));

        PageTraversal traversal = route.getPageTraversal();
        pageClass = traversal.getMethod().getReturnType();
        assertThat(pageClass == CircularHomePage.class, is(true));
    }

    @Page
    @CrawlStartPoint(url="")
    private static class CircularHomePage {
        @SubmitAction
        public CircularHomePage submit() { return this; }
    }

    @Page
    private class OrdinaryPage {}

    @Page
    @CrawlStartPoint(url="")
    private class StartPageOne {
        public OrdinaryPage goToOrdinaryPage() { return null; }
    }

    @Page
    @CrawlStartPoint(url="")
    private class StartPageTwo {}

    @Page
    @CrawlStartPoint(url="")
    private class ForkStartPage {
        public ForkChildPageOne gotoFirstChild() { return null; }
        public ForkChildPageTwo gotoSecondChild() { return null; }
    }

    @Page
    private class ForkChildPageOne {}

    @Page
    private class ForkChildPageTwo {}

    @Page
    @CrawlStartPoint(url="")
    private class LoginPage {
        public SignUpPage register() { return null; }
    }

    @Page
    private class SignUpPage {
        @SubmitAction
        public LoginPage submit() { return null; }
        public LoginPage cancel() { return null; }
    }
}
