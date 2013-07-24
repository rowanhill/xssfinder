package org.xssfinder.routing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouteFactoryTest {
    @Mock
    private PageTraversalFactory mockPageTraversalFactory;

    @Mock
    private GraphNode mockNode;
    @Mock
    private PageDescriptor mockPageDescriptor;

    @InjectMocks
    private RouteFactory routeFactory;

    @Before
    public void setUp() {
        when(mockNode.getPageDescriptor()).thenReturn(mockPageDescriptor);
    }

    @Test
    public void nodeWithNoPredecessorCreatesRouteStartingAtNode() {
        // when
        Route route = routeFactory.createRouteEndingAtNode(mockNode);

        // then
        assertThat(route.getRootPageDescriptor(), is(mockPageDescriptor));
        assertThat(route.getPageTraversal(), is(nullValue()));
    }

    @Test
    public void nodeWithPredecessorCreatesRouteStartingAtPredecessor() {
        // given
        GraphNode mockPredecessorNode = mock(GraphNode.class);
        PageDescriptor mockPredecessorPageDescriptor = mock(PageDescriptor.class);
        when(mockPredecessorNode.getPageDescriptor()).thenReturn(mockPredecessorPageDescriptor);
        when(mockNode.getPredecessor()).thenReturn(mockPredecessorNode);
        when(mockNode.hasPredecessor()).thenReturn(true);
        PageTraversal mockTraversal = mock(PageTraversal.class);
        when(mockPageTraversalFactory.createTraversalToNode(mockNode, PageTraversal.TraversalMode.NORMAL))
                .thenReturn(mockTraversal);

        // when
        Route route = routeFactory.createRouteEndingAtNode(mockNode);

        // then
        assertThat(route.getRootPageDescriptor(), is(mockPredecessorPageDescriptor));
        assertThat(route.getPageTraversal(), is(mockTraversal));
    }
}
