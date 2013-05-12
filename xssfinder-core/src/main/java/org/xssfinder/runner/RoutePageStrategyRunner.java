package org.xssfinder.runner;

import org.xssfinder.routing.PageTraversal;
import org.xssfinder.routing.Route;

import java.util.List;

public class RoutePageStrategyRunner {
    private final DriverWrapper driverWrapper;
    private final PageInstantiator pageInstantiator;
    private final PageTraverser pageTraverser;

    public RoutePageStrategyRunner(DriverWrapper driverWrapper, PageInstantiator pageInstantiator, PageTraverser pageTraverser) {
        this.driverWrapper = driverWrapper;
        this.pageInstantiator = pageInstantiator;
        this.pageTraverser = pageTraverser;
    }

    public void run(List<Route> routes, List<PageStrategy> pageStrategies) {
        for (Route route : routes) {
            driverWrapper.visit(route.getUrl());
            Object page = pageInstantiator.instantiatePage(route.getRootPageClass());

            PageTraversal traversal = route.getPageTraversal();
            while (traversal != null) {
                executePageStrategies(pageStrategies, page, traversal);
                page = pageTraverser.traverse(page, traversal);
                traversal = traversal.getNextTraversal();
            }
            executePageStrategies(pageStrategies, page, traversal);
        }
    }

    private void executePageStrategies(List<PageStrategy> pageStrategies, Object page, PageTraversal traversal) {
        for (PageStrategy pageStrategy : pageStrategies) {
            pageStrategy.processPage(page, traversal, driverWrapper);
        }
    }
}
