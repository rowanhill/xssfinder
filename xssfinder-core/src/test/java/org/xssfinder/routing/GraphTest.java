package org.xssfinder.routing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.PageDefinition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.xssfinder.testhelper.MockPageDefinitionBuilder.mockPageDefinition;
import static org.xssfinder.testhelper.MockPageDescriptorBuilder.mockPageDescriptor;

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
        PageDefinition mockOrdinaryPageDefinition = mockPageDefinition().build();
        PageDefinition mockStartPage1Definition = mockPageDefinition()
                .markedAsCrawlStartPoint()
                .withMethod().toPage(mockOrdinaryPageDefinition).onPage()
                .build();
        PageDefinition mockStartPage2Definition = mockPageDefinition().markedAsCrawlStartPoint().build();

        ordinaryPageDescriptor = mockPageDescriptor(mockOrdinaryPageDefinition);
        startPage1Descriptor = mockPageDescriptor(mockStartPage1Definition);
        startPage2Descriptor = mockPageDescriptor(mockStartPage2Definition);
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
        DjikstraResult mockDjikstraResult = mock(DjikstraResult.class);
        Set<GraphNode> leafNodes = new HashSet<GraphNode>();
        when(mockDjikstraResult.getLeafNodes()).thenReturn(leafNodes);
        when(mockDjikstraRunner.computeShortestPaths(startPage1Descriptor.getPageDefinition(), pagesDescriptors))
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
}
