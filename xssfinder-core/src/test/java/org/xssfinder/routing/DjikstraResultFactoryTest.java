package org.xssfinder.routing;

import org.junit.Test;
import org.xssfinder.remote.PageDefinition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class DjikstraResultFactoryTest {
    @Test
    public void createsDjikstraResult() {
        // given
        Map<String, GraphNode> classesToNodes = new HashMap<String, GraphNode>();
        Set<GraphNode> leafNodes = new HashSet<GraphNode>();
        DjikstraResultFactory factory = new DjikstraResultFactory();

        // when
        DjikstraResult result = factory.createResult(classesToNodes, leafNodes);

        // then
        assertThat(result, is(notNullValue()));
    }
}
