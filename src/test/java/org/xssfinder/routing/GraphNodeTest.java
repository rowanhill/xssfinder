package org.xssfinder.routing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;

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
    public void predecessorCanBeSet() {
        // given
        GraphNode node = new GraphNode(mockPageDescriptor);
        GraphNode otherNode = mock(GraphNode.class);

        // when
        node.setPredecessor(otherNode);

        // then
        assertThat(node.getPredecessor(), is(otherNode));
    }

    @Test
    public void pageClassIsDelegatedToPageDescriptor() {
        // given
        Class ordinaryPageClass = OrdinaryPage.class;
        when(mockPageDescriptor.getPageClass()).thenReturn(ordinaryPageClass);
        GraphNode node = new GraphNode(mockPageDescriptor);

        // when
        Class<?> pageClass = node.getPageClass();

        // then
        assertThat(pageClass == ordinaryPageClass, is(true));
    }

    @Page
    private class OrdinaryPage {}

    @Page
    @CrawlStartPoint
    private class StartPointPage {}

}
