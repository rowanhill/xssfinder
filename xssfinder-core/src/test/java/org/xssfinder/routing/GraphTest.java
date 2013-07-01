package org.xssfinder.routing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphTest {
    @Mock
    private DjikstraRunner mockDjikstraRunner;
    @Mock
    private RequiredTraversalAppender mockRequiredTraversalAppender;

    private PageDescriptor ordinaryPageDescriptor;
    private PageDescriptor startPage1Descriptor;
    private PageDescriptor startPage2Descriptor;

    private final Set<PageDescriptor> pagesDescriptors = new HashSet<PageDescriptor>();

    @Before
    public void setup() {
        ordinaryPageDescriptor = new PageDescriptor(OrdinaryPage.class);
        startPage1Descriptor = new PageDescriptor(StartPageOne.class);
        startPage2Descriptor = new PageDescriptor(StartPageTwo.class);
    }

    @Test(expected=NoRootPageFoundException.class)
    public void constructorThrowsExceptionIfNoRootNodeIsFound() {
        // given
        pagesDescriptors.add(ordinaryPageDescriptor);

        // when
        constructGraph();
    }

    @Test(expected=MultipleRootPagesFoundException.class)
    public void constructorThrowsExceptionIfMultipleRootNodesAreFound() {
        // given
        pagesDescriptors.add(startPage1Descriptor);
        pagesDescriptors.add(startPage2Descriptor);

        // when
        constructGraph();
    }

    @Test
    public void routesAreCreatedFromShortestPathsWithRequiredTraversalsAppended() {
        // given
        pagesDescriptors.add(startPage1Descriptor);
        Class<?> rootClass = startPage1Descriptor.getPageClass();
        DjikstraResult mockDjikstraResult = mock(DjikstraResult.class);
        Set<GraphNode> leafNodes = new HashSet<GraphNode>();
        when(mockDjikstraResult.getLeafNodes()).thenReturn(leafNodes);
        when(mockDjikstraRunner.computeShortestPaths(rootClass, pagesDescriptors))
                .thenReturn(mockDjikstraResult);
        List<Route> leafNodeRoutes = new ArrayList<Route>();
        when(mockDjikstraResult.getRoutesToLeafNodes()).thenReturn(leafNodeRoutes);
        List<Route> appendedRoutes = new ArrayList<Route>();
        when(mockRequiredTraversalAppender.appendTraversalsToRoutes(leafNodeRoutes, pagesDescriptors, mockDjikstraResult))
                .thenReturn(appendedRoutes);
        Graph graph = constructGraph();

        // when
        List<Route> routes = graph.getRoutes();

        // then
        assertThat(routes, is(appendedRoutes));
    }

    private Graph constructGraph() {
        return new Graph(pagesDescriptors, mockDjikstraRunner, mockRequiredTraversalAppender);
    }

    @Page
    private class OrdinaryPage {}

    @SuppressWarnings("UnusedDeclaration")
    @Page
    @CrawlStartPoint(url="")
    private class StartPageOne {
        public OrdinaryPage goToOrdinaryPage() { return null; }
    }

    @Page
    @CrawlStartPoint(url="")
    private class StartPageTwo {}
}
