package org.xssfinder.routing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphNodeTest {

    @Mock
    private PageDescriptor mockPageDescriptor;

    @Test
    public void distanceFromRootDefaultsToIntMax() {
        // given
        GraphNode node = new GraphNode(mockPageDescriptor);

        // when
        int distance = node.getDistance();

        // then
        assertThat(distance, is(Integer.MAX_VALUE));
    }

    @Test
    public void canSetDistanceFromRoot() {
        // given
        GraphNode node = new GraphNode(mockPageDescriptor);

        // when
        node.setDistance(1);

        // then
        assertThat(node.getDistance(), is(1));
    }

    @Test
    public void isRootDelegatesToPageDescriptor() {
        // given
        when(mockPageDescriptor.isRoot()).thenReturn(true);
        GraphNode node = new GraphNode(mockPageDescriptor);

        // when
        boolean isRoot = node.isRoot();

        // then
        verify(mockPageDescriptor).isRoot();
        assertThat(isRoot, is(true));
    }

    @Test
    public void predecessorDefaultsToNull() {
        // given
        GraphNode node = new GraphNode(mockPageDescriptor);

        // when
        GraphNode predecessor = node.getPredecessor();

        // then
        assertThat(predecessor, is(nullValue(GraphNode.class)));
    }

    @Test
    public void predecessorAndMethodCanBeSet() throws Exception {
        // given
        GraphNode node = new GraphNode(mockPageDescriptor);
        GraphNode otherNode = mock(GraphNode.class);
        MethodDefinition mockMethodDefinition = mock(MethodDefinition.class);

        // when
        node.setPredecessor(otherNode, mockMethodDefinition);

        // then
        assertThat(node.getPredecessor(), is(otherNode));
        assertThat(node.getPredecessorTraversalMethod(), is(mockMethodDefinition));
    }

    @Test
    public void hasNoPredecessorIfSetToNull() {
        // given
        GraphNode node = new GraphNode(mockPageDescriptor);

        // when
        boolean hasPredecessor = node.hasPredecessor();

        // then
        assertThat(hasPredecessor, is(false));
    }

    @Test
    public void hasPredecessorIfSet() throws Exception {
        // given
        GraphNode node = new GraphNode(mockPageDescriptor);
        GraphNode otherNode = mock(GraphNode.class);
        MethodDefinition mockMethodDefinition = mock(MethodDefinition.class);
        node.setPredecessor(otherNode, mockMethodDefinition);

        // when
        boolean hasPredecessor = node.hasPredecessor();

        // then
        assertThat(hasPredecessor, is(true));
    }

    @Test
    public void pageDefinitionIsDelegatedToPageDescriptor() {
        // given
        PageDefinition mockPageDefinition = mock(PageDefinition.class);
        when(mockPageDescriptor.getPageDefinition()).thenReturn(mockPageDefinition);
        GraphNode node = new GraphNode(mockPageDescriptor);

        // when
        PageDefinition pageDefinition = node.getPageDefinition();

        // then
        assertThat(pageDefinition, is(mockPageDefinition));
    }

    @Test
    public void pageDescriptorIsAvailable() {
        // given
        GraphNode node = new GraphNode(mockPageDescriptor);

        // when
        PageDescriptor pageDescriptor = node.getPageDescriptor();

        // then
        assertThat(pageDescriptor, is(mockPageDescriptor));
    }
}
