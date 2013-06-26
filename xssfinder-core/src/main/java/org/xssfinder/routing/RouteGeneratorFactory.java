package org.xssfinder.routing;

import org.xssfinder.reflection.Instantiator;

/**
 * Factory for RouteGenerator; essentially just dependency injection container
 */
public class RouteGeneratorFactory {
    public RouteGenerator createRouteGenerator() {
        GraphsFactory graphsFactory = new GraphsFactory(
                new DjikstraRunner(new GraphNodesFactory()),
                new LeafNodeRouteFactory(new Instantiator(), new PageTraversalFactory()),
                new RequiredTraversalAppender(new UntraversedSubmitMethodsFinder())
        );
        return new RouteGenerator(graphsFactory);
    }
}
