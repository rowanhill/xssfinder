package org.xssfinder.runner;

import org.xssfinder.routing.PageTraversal;
import org.xssfinder.routing.Route;

import java.lang.reflect.Method;
import java.util.List;

public class RouteRunner {
    private final DriverWrapper driverWrapper;
    private final PageInstantiator pageInstantiator;
    private final List<Route> routes;

    public RouteRunner(DriverWrapper driverWrapper, PageInstantiator pageInstantiator, List<Route> routes) {
        this.driverWrapper = driverWrapper;
        this.pageInstantiator = pageInstantiator;
        this.routes = routes;
    }

    public void run() {
        for (Route route : routes) {
            driverWrapper.visit(route.getUrl());

            Object page = pageInstantiator.instantiatePage(route.getRootPageClass());

            PageTraversal traversal = route.getPageTraversal();
            do {
                //TODO Perform XSS attacks on page
                if (traversal != null) {
                    page = traverseToNextPage(page, traversal.getMethod());
                    traversal = traversal.getNextTraversal();
                }
            } while (traversal != null);
        }
    }

    private Object traverseToNextPage(Object page, Method method) {
        if (method.getParameterTypes().length > 0) {
            throw new UntraversableException("Cannot traverse methods that take parameters");
        }
        try {
            return method.invoke(page);
        } catch (Exception e) {
            throw new UntraversableException(e);
        }
    }
}
