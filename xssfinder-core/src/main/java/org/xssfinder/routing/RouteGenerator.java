package org.xssfinder.routing;

import org.xssfinder.remote.PageDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RouteGenerator {
    private final GraphsFactory graphsFactory;

    public RouteGenerator(GraphsFactory graphsFactory) {
        this.graphsFactory = graphsFactory;
    }

    public List<Route> generateRoutes(Set<PageDefinition> pageClasses) {
        Set<Graph> graphs = graphsFactory.createGraphs(pageClasses);
        List<Route> routes = new ArrayList<Route>();

        for (Graph graph : graphs) {
            routes.addAll(graph.getRoutes());
        }

        return routes;
    }
}
