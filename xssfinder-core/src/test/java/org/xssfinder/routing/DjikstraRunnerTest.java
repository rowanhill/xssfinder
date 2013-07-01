package org.xssfinder.routing;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.xssfinder.Page;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DjikstraRunnerTest {
    @Test
    public void findsShortestPathsToLeafNodes() {
        // given
        Set<PageDescriptor> descriptors = ImmutableSet.of(
                new PageDescriptor(RootPage.class),
                new PageDescriptor(MiddlePage.class),
                new PageDescriptor(DetourPageOne.class),
                new PageDescriptor(DetourPageTwo.class),
                new PageDescriptor(LeafPageOne.class),
                new PageDescriptor(LeafPageTwo.class)
        );
        GraphNodesFactory nodesFactory = new GraphNodesFactory();
        DjikstraResultFactory djikstraResultFactory = new DjikstraResultFactory();
        DjikstraRunner runner = new DjikstraRunner(nodesFactory, djikstraResultFactory);

        // when
        DjikstraResult djikstraResult = runner.computeShortestPaths(RootPage.class, descriptors);
        Set<GraphNode> leafNodes = djikstraResult.getLeafNodes();

        // then
        assertThat(leafNodes.size(), is(4));
        for (GraphNode node : leafNodes) {
            assertThat(node.getDistance(), is(2));
            assertThat(node.getPredecessor().getPageClass() == MiddlePage.class, is(true));
        }
    }

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
    @SuppressWarnings("UnusedDeclaration")
    @Page
    private static class RootPage {
        public MiddlePage getMiddlePage() { return null; }
    }
    @SuppressWarnings("UnusedDeclaration")
    @Page
    private static class MiddlePage {
        public DetourPageOne getDetourPageOne() { return null; }
        public DetourPageTwo getDetourPageTwo() { return null; }
        public LeafPageOne getLeafPageOne() { return null; }
        public LeafPageTwo getLeafPageTwo() { return null; }
    }
    @SuppressWarnings("UnusedDeclaration")
    @Page
    private static class DetourPageOne {
        public LeafPageOne getLeafPageOne() { return null; }
    }
    @Page
    private static class LeafPageOne {}
    @SuppressWarnings("UnusedDeclaration")
    @Page
    private static class DetourPageTwo {
        public LeafPageTwo getLeafPageTwo() { return null; }
    }
    @Page
    private static class LeafPageTwo {}
}
