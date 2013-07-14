package org.xssfinder.routing;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.PageDefinition;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.xssfinder.testhelper.MockPageDefinitionBuilder.mockPageDefinition;

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
        Set<PageDefinition> pages = ImmutableSet.of();

        // when
        Set<Graph> graphs = factory.createGraphs(pages);

        // then
        Set<Graph> emptySetOfGraphs = ImmutableSet.of();
        assertThat(graphs, is(emptySetOfGraphs));
    }

    @Test
    public void singleGraphReturnedFromSimpleSetOfPages() {
        // given
        PageDefinition mockSecondPage = mockPageDefinition().build();
        PageDefinition mockHomePage = mockPageDefinition()
                .markedAsCrawlStartPoint()
                .withMethod()
                    .toPage(mockSecondPage)
                    .onPage()
                .build();

        Set<PageDefinition> pages = ImmutableSet.of(mockHomePage, mockSecondPage);

        // when
        Set<Graph> graphs = factory.createGraphs(pages);

        // then
        assertThat(graphs.size(), is(1));
    }
}
