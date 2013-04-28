package org.xssfinder.routing;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GraphTest {
    private PageDescriptor ordinaryPageDescriptor;
    private PageDescriptor startPage1Descriptor;
    private PageDescriptor startPage2Descriptor;
    private PageDescriptor forkStartPageDescriptor;
    private PageDescriptor forkChildPageOneDescriptor;
    private PageDescriptor forkChildPageTwoDescriptor;

    private Set<PageDescriptor> pagesDescriptors = new HashSet<PageDescriptor>();

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
        new Graph(pagesDescriptors);
    }

    @Test(expected=MultipleRootPagesFoundException.class)
    public void constructorThrowsExceptionIfMultipleRootNodesAreFound() {
        // given
        pagesDescriptors.add(startPage1Descriptor);
        pagesDescriptors.add(startPage2Descriptor);

        // when
        new Graph(pagesDescriptors);
    }

    @Test
    public void simpleLinearlyRelatedPagesReturnsOneRoute() {
        // given
        pagesDescriptors.add(startPage1Descriptor);
        pagesDescriptors.add(ordinaryPageDescriptor);
        Graph graph = new Graph(pagesDescriptors);

        // when
        List<Route> routes = graph.getRoutes();

        // then
        assertThat(routes.size(), is(1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void forkingPagesReturnTwoRoutes() {
        // given
        pagesDescriptors.add(forkStartPageDescriptor);
        pagesDescriptors.add(forkChildPageOneDescriptor);
        pagesDescriptors.add(forkChildPageTwoDescriptor);
        Graph graph = new Graph(pagesDescriptors);

        // when
        List<Route> routes = graph.getRoutes();

        // then
        assertThat(routes.size(), is(2));
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
}
