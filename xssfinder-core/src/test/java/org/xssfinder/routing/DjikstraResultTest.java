package org.xssfinder.routing;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DjikstraResultTest {
    @Test
    public void classesToNodesAndLeafNodesAreAvailable() {
        // given
        Map<Class<?>, GraphNode> classesToNodes = new HashMap<Class<?>, GraphNode>();
        Set<GraphNode> leafNodes = new HashSet<GraphNode>();
        DjikstraResult djikstraResult = new DjikstraResult(classesToNodes, leafNodes);

        // when
        Map<Class<?>, GraphNode> resultClassesToNodes = djikstraResult.getClassesToNodes();
        Set<GraphNode> resultLeafNodes = djikstraResult.getLeafNodes();

        // then
        assertThat(resultClassesToNodes, is(classesToNodes));
        assertThat(resultLeafNodes, is(leafNodes));
    }
}
