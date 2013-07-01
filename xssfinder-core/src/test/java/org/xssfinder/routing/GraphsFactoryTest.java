package org.xssfinder.routing;

import com.google.common.collect.ImmutableSet;
import org.dummytest.simple.HomePage;
import org.dummytest.simple.SecondPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GraphsFactoryTest {
    @Mock
    private DjikstraRunner mockDjikstraRunner;
    @Mock
    private RequiredTraversalAppender mockRequiredTraversalAppender;

    @InjectMocks
    GraphsFactory factory;

    @Test
    public void noGraphsCreatedFromEmptySetOfPages() {
        // given
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
        Set<Class<?>> pages = ImmutableSet.of(HomePage.class, SecondPage.class);

        // when
        Set<Graph> graphs = factory.createGraphs(pages);

        // then
        assertThat(graphs.size(), is(1));
    }
}
