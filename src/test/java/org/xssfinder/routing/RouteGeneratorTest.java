package org.xssfinder.routing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouteGeneratorTest {
    @Mock
    private GraphsFactory mockGraphsFactory;
    @Mock
    private Graph mockGraph;

    @Test
    public void returnsRoutesFromSingleGraph() {
        // given
        RouteGenerator generator = new RouteGenerator(mockGraphsFactory);
        Set<Graph> graphs = ImmutableSet.of(mockGraph);
        Set<Class<?>> pageClasses = ImmutableSet.of(OrdinaryPage.class, StartPage.class);
        when(mockGraphsFactory.createGraphs(pageClasses)).thenReturn(graphs);
        Route route = mock(Route.class);
        List<Route> graphRoutes = ImmutableList.of(route);
        when(mockGraph.getRoutes()).thenReturn(graphRoutes);

        // when
        List<Route> routes = generator.generateRoutes(pageClasses);

        // then
        assertThat(routes, is(graphRoutes));
    }

    @Page
    private class OrdinaryPage {}

    @Page
    @CrawlStartPoint(url="")
    private class StartPage {}
}
