package org.xssfinder.routing;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class DjikstraResultFactoryTest {
    @Test
    public void createsDjikstraResult() {
        // given
        Map<Class<?>, GraphNode> classesToNodes = new HashMap<Class<?>, GraphNode>();
        Set<GraphNode> leafNodes = new HashSet<GraphNode>();
        DjikstraResultFactory factory = new DjikstraResultFactory();

        // when
        DjikstraResult result = factory.createResult(classesToNodes, leafNodes);

        // then
        assertThat(result, is(notNullValue()));
    }
}
