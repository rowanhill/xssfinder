package org.xssfinder.routing;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.xssfinder.remote.PageDefinition;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GraphNodesFactoryTest {
    @Test
    public void createsNodesFromPageDescriptors() {
        // given
        PageDescriptor mockPageDescriptor = mock(PageDescriptor.class);
        PageDefinition mockPageDefinition = mock(PageDefinition.class);
        when(mockPageDescriptor.getPageDefinition()).thenReturn(mockPageDefinition);
        when(mockPageDefinition.getIdentifier()).thenReturn("SomePage");
        Set<PageDescriptor> descriptors = ImmutableSet.of(mockPageDescriptor);
        GraphNodesFactory factory = new GraphNodesFactory();

        // when
        Map<String, GraphNode> nodes = factory.createNodes(descriptors);

        // then
        assertThat(nodes.size(), is(1));
        assertThat(nodes, hasKey("SomePage"));
        assertThat(nodes.get("SomePage").getPageDescriptor(), is(mockPageDescriptor));
    }
}
