package org.xssfinder.runner;

import org.xssfinder.routing.PageTraversal;
import org.xssfinder.routing.Route;

import java.util.List;

public class RouteRunner {
    private final DriverWrapper driverWrapper;
    private final PageInstantiator pageInstantiator;
    private final PageTraverser pageTraverser;
    private final List<Route> routes;

    public RouteRunner(
            DriverWrapper driverWrapper,
            PageInstantiator pageInstantiator,
            PageTraverser pageTraverser,
            List<Route> routes
    ) {
        this.driverWrapper = driverWrapper;
        this.pageInstantiator = pageInstantiator;
        this.pageTraverser = pageTraverser;
        this.routes = routes;
    }

    public void run() {
        for (Route route : routes) {
            driverWrapper.visit(route.getUrl());

            Object page = pageInstantiator.instantiatePage(route.getRootPageClass());

            PageTraversal traversal = route.getPageTraversal();
            //TODO Warn of missing @SubmitAction if needed
            while (traversal != null) {
                //TODO If @SubmitAction
                //TODO Perform XSS attacks on page
                //TODO Else warn of missing @SubmitAction if needed
                page = pageTraverser.traverse(page, traversal);
                traversal = traversal.getNextTraversal();
            }
        }
    }
}
