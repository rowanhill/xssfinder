package org.xssfinder.routing;

import com.google.common.collect.ImmutableSet;
import org.dummytest.simple.HomePage;
import org.dummytest.simple.SecondPage;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class GraphsFactoryTest {
    @Test
    public void noGraphsCreatedFromEmptySetOfPages() {
        // given
        GraphsFactory factory = new GraphsFactory();
        Set<Class<?>> pages = ImmutableSet.of();

        // when
        Set<Graph> graphs = factory.createGraphs(pages);

        // then
        Set<Graph> emptySetOfGraphs = ImmutableSet.of();
        assertThat(graphs, is(emptySetOfGraphs));
    }

    @Test
    public void singleGraphReturnedFromSimpleSetOfPages() {
        // given
        GraphsFactory factory = new GraphsFactory();
        Set<Class<?>> pages = ImmutableSet.of(HomePage.class, SecondPage.class);

        // when
        Set<Graph> graphs = factory.createGraphs(pages);

        // then
        assertThat(graphs.size(), is(1));
    }
}
