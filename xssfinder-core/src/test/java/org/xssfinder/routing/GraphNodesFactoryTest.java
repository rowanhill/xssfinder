package org.xssfinder.routing;

import com.google.common.collect.ImmutableSet;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GraphNodesFactoryTest {
    @SuppressWarnings("unchecked")
    @Test
    public void createsNodesFromPageDescriptors() {
        // given
        PageDescriptor mockPageDescriptor = mock(PageDescriptor.class);
        when(mockPageDescriptor.getPageClass()).thenReturn((Class)SomePage.class);
        Set<PageDescriptor> descriptors = ImmutableSet.of(mockPageDescriptor);
        GraphNodesFactory factory = new GraphNodesFactory();

        // when
        Map<Class<?>, GraphNode> nodes = factory.createNodes(descriptors);

        // then
        assertThat(nodes.size(), is(1));
        assertThat(nodes, (Matcher)hasKey(SomePage.class));
        assertThat(nodes.get(SomePage.class).getPageDescriptor(), is(mockPageDescriptor));
    }

    private static class SomePage {}
}
