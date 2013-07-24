package org.xssfinder.routing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.MethodDefinition;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageTraversalFactoryTest {
    @Mock
    private MethodDefinition mockMethodDefinition;

    @Test
    public void creatingTraversalFromNodeUsesNodePredecessorTraversalMethodAndPageDescriptor() throws Exception {
        // given
        PageTraversalFactory factory = new PageTraversalFactory();
        GraphNode mockNode = mock(GraphNode.class);
        when(mockNode.getPredecessorTraversalMethod()).thenReturn(mockMethodDefinition);
        PageDescriptor mockPageDescriptor = mock(PageDescriptor.class);
        when(mockNode.getPageDescriptor()).thenReturn(mockPageDescriptor);

        // when
        PageTraversal traversal = factory.createTraversalToNode(mockNode, PageTraversal.TraversalMode.NORMAL);

        // then
        assertThat(traversal.getMethod(), is(mockMethodDefinition));
        assertThat(traversal.getResultingPageDescriptor(), is(mockPageDescriptor));
    }

    @Test
    public void creatingTraversalFromMethodAndDescriptorUsesThoseGiven() throws Exception {
        // given
        PageTraversalFactory factory = new PageTraversalFactory();
        PageDescriptor mockPageDescriptor = mock(PageDescriptor.class);
        PageTraversal.TraversalMode traversalMode = PageTraversal.TraversalMode.NORMAL;

        // when
        PageTraversal traversal = factory.createTraversal(mockMethodDefinition, mockPageDescriptor, traversalMode);

        // then
        assertThat(traversal.getMethod(), is(mockMethodDefinition));
        assertThat(traversal.getResultingPageDescriptor(), is(mockPageDescriptor));
        assertThat(traversal.getTraversalMode(), is(traversalMode));
    }
}
