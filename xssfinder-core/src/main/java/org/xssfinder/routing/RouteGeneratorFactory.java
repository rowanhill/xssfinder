package org.xssfinder.routing;

/**
 * Factory for RouteGenerator; essentially just dependency injection container
 */
public class RouteGeneratorFactory {
    public RouteGenerator createRouteGenerator() {
        GraphsFactory graphsFactory = new GraphsFactory(
                new DjikstraRunner(new GraphNodesFactory(), new DjikstraResultFactory()),
                new RequiredTraversalAppender(new UntraversedSubmitMethodsFinder())
        );
        return new RouteGenerator(graphsFactory);
    }
}
