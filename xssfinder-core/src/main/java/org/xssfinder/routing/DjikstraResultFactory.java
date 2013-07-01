package org.xssfinder.routing;

import org.xssfinder.reflection.Instantiator;

import java.util.Map;
import java.util.Set;

public class DjikstraResultFactory {
    public DjikstraResult createResult(Map<Class<?>, GraphNode> classesToNodes, Set<GraphNode> leafNodes) {
        RouteFactory routeFactory = new RouteFactory(
                new Instantiator(),
                new PageTraversalFactory()
        );
        return new DjikstraResult(routeFactory, classesToNodes, leafNodes);
    }
}
