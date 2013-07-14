package org.xssfinder.routing;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.xssfinder.testhelper.MockPageDefinitionBuilder.mockPageDefinition;
import static org.xssfinder.testhelper.MockPageDescriptorBuilder.mockPageDescriptor;

@RunWith(MockitoJUnitRunner.class)
public class DjikstraRunnerTest {
    /**
     * The page network is arranged as below:
     *
     *         LeafOne         LeafTwo
     *        /      \        /       \
     *  DetourOne     \      /       DetourTwo
     *    |            \    /              |
     *    |-------------Middle-------------|
     *                    |
     *                   Root
     */

    private PageDefinition mockRootPageDefinition;
    private PageDefinition mockMiddlePageDefinition;
    private PageDefinition mockDetourOnePageDefinition;
    private PageDefinition mockDetourTwoPageDefinition;
    private PageDefinition mockLeafOnePageDefinition;
    private PageDefinition mockLeafTwoPageDefinition;

    @Before
    public void setUp() {
        mockLeafOnePageDefinition = mockPageDefinition("Leaf 1").build();
        mockLeafTwoPageDefinition = mockPageDefinition("Leaf 2").build();
        mockDetourOnePageDefinition = mockPageDefinition("Detour 1")
                .withMethod().toPage(mockLeafOnePageDefinition).onPage()
                .build();
        mockDetourTwoPageDefinition = mockPageDefinition("Detour 2")
                .withMethod().toPage(mockLeafTwoPageDefinition).onPage()
                .build();
        mockMiddlePageDefinition = mockPageDefinition("Middle")
                .withMethod().toPage(mockDetourOnePageDefinition).onPage()
                .withMethod().toPage(mockLeafOnePageDefinition).onPage()
                .withMethod().toPage(mockDetourTwoPageDefinition).onPage()
                .withMethod().toPage(mockLeafTwoPageDefinition).onPage()
                .build();
        mockRootPageDefinition = mockPageDefinition("Root")
                .withMethod().toPage(mockMiddlePageDefinition).onPage()
                .build();
    }

    @Test
    public void findsShortestPathsToLeafNodes() {
        // given
        Set<PageDescriptor> descriptors = ImmutableSet.of(
                mockPageDescriptor(mockRootPageDefinition),
                mockPageDescriptor(mockMiddlePageDefinition),
                mockPageDescriptor(mockDetourOnePageDefinition),
                mockPageDescriptor(mockDetourTwoPageDefinition),
                mockPageDescriptor(mockLeafOnePageDefinition),
                mockPageDescriptor(mockLeafTwoPageDefinition)
        );
        GraphNodesFactory nodesFactory = new GraphNodesFactory();
        DjikstraResultFactory djikstraResultFactory = new DjikstraResultFactory();
        DjikstraRunner runner = new DjikstraRunner(nodesFactory, djikstraResultFactory);

        // when
        DjikstraResult djikstraResult = runner.computeShortestPaths(mockRootPageDefinition, descriptors);
        Set<GraphNode> leafNodes = djikstraResult.getLeafNodes();

        // then
        assertThat(leafNodes.size(), is(4));
        for (GraphNode node : leafNodes) {
            assertThat(node.getDistance(), is(2));
            assertThat(node.getPredecessor().getPageDescriptor().getPageDefinition(), is(mockMiddlePageDefinition));
        }
    }
}
