package org.xssfinder.routing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.reflection.Instantiator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LeafNodeRouteFactoryTest {
    @Mock
    private Instantiator instantiator;
    @Mock
    private PageTraversalFactory mockPageTraversalFactory;

    @InjectMocks
    private LeafNodeRouteFactory leafNodeRouteFactory;

    @Test
    public void noRoutesCreatedFromEmptySetOfLeafNodes() {
        // given
        Set<GraphNode> graphNodes = new HashSet<GraphNode>();

        // when
        List<Route> routes = leafNodeRouteFactory.getRoutesFromLeafNodes(graphNodes);

        // then
        assertThat(routes, is(empty()));
    }

    @Test
    public void singleLeafNodeWithNoPredecessorProducesSingleRouteStartingAtNode() {
        // given
        Set<GraphNode> graphNodes = new HashSet<GraphNode>();
        GraphNode mockNode = addMockGraphNode(graphNodes);
        PageDescriptor mockPageDescriptor = addMockPageDescriptor(mockNode);

        // when
        List<Route> routes = leafNodeRouteFactory.getRoutesFromLeafNodes(graphNodes);

        // then
        assertThat(routes.size(), is(1));
        Route route = routes.get(0);
        assertThat(route.getRootPageDescriptor(), is(mockPageDescriptor));
        assertThat(route.getPageTraversal(), is(nullValue()));
    }

    @Test
    public void singleLeafNodeWithPredecessorProducesSingleRouteStartingAtPredecessor() {
        // given
        Set<GraphNode> graphNodes = new HashSet<GraphNode>();
        GraphNode mockNode = addMockGraphNodeWithPageDescriptor(graphNodes);
        GraphNode mockPredecessorNode = addPredecessorToNode(mockNode);
        PageTraversal mockTraversal = mockTraversalForNode(mockNode);

        // when
        List<Route> routes = leafNodeRouteFactory.getRoutesFromLeafNodes(graphNodes);

        // then
        verify(mockTraversal).setNextTraversal(null);
        assertThat(routes.size(), is(1));
        Route route = routes.get(0);
        assertThat(route.getRootPageDescriptor(), is(mockPredecessorNode.getPageDescriptor()));
        assertThat(route.getPageTraversal(), is(mockTraversal));
    }

    @Test
    public void multipleLeafNodesProduceSameNumberOfRoutes() {
        // given
        Set<GraphNode> graphNodes = new HashSet<GraphNode>();
        addMockGraphNodeWithPageDescriptor(graphNodes);
        addMockGraphNodeWithPageDescriptor(graphNodes);
        addMockGraphNodeWithPageDescriptor(graphNodes);

        // when
        List<Route> routes = leafNodeRouteFactory.getRoutesFromLeafNodes(graphNodes);

        // then
        assertThat(routes.size(), is(3));
    }

    private GraphNode addMockGraphNodeWithPageDescriptor(Set<GraphNode> leafNodes) {
        GraphNode mockNode = addMockGraphNode(leafNodes);
        addMockPageDescriptor(mockNode);
        return mockNode;
    }

    private GraphNode addMockGraphNode(Set<GraphNode> leafNodes) {
        GraphNode mockNode = mock(GraphNode.class);
        leafNodes.add(mockNode);
        return mockNode;
    }

    private GraphNode addPredecessorToNode(GraphNode mockNode) {
        GraphNode mockPredecessorNode = mock(GraphNode.class);
        addMockPageDescriptor(mockPredecessorNode);
        when(mockNode.getPredecessor()).thenReturn(mockPredecessorNode);
        when(mockNode.hasPredecessor()).thenReturn(true);
        return mockPredecessorNode;
    }

    private PageDescriptor addMockPageDescriptor(GraphNode mockNode) {
        PageDescriptor mockPageDescriptor = mock(PageDescriptor.class);
        when(mockNode.getPageDescriptor()).thenReturn(mockPageDescriptor);
        return mockPageDescriptor;
    }

    private PageTraversal mockTraversalForNode(GraphNode node) {
        PageTraversal mockTraversal = mock(PageTraversal.class);
        when(mockPageTraversalFactory.createTraversalToNode(node)).thenReturn(mockTraversal);
        return mockTraversal;
    }
}
