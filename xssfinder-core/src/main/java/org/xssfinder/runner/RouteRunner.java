package org.xssfinder.runner;

import org.xssfinder.routing.PageTraversal;
import org.xssfinder.routing.Route;
import org.xssfinder.xss.XssGenerator;

import java.util.List;

public class RouteRunner {
    private final DriverWrapper driverWrapper;
    private final XssGenerator xssGenerator;
    private final PageInstantiator pageInstantiator;
    private final PageTraverser pageTraverser;
    private final List<Route> routes;

    public RouteRunner(
            DriverWrapper driverWrapper,
            PageTraverser pageTraverser,
            XssGenerator xssGenerator,
            List<Route> routes
    ) {
        this.driverWrapper = driverWrapper;
        this.pageInstantiator = driverWrapper.getPageInstantiator();
        this.pageTraverser = pageTraverser;
        this.xssGenerator = xssGenerator;
        this.routes = routes;
    }

    public void run() {
        for (Route route : routes) {
            driverWrapper.visit(route.getUrl());

            Object page = pageInstantiator.instantiatePage(route.getRootPageClass());

            PageTraversal traversal = route.getPageTraversal();
            //TODO Warn of missing @SubmitAction if needed
            while (traversal != null) {
                if (traversal.isSubmit()) {
                    driverWrapper.putXssAttackStringsInInputs(xssGenerator);
                }
                //TODO Else warn of missing @SubmitAction if needed
                //TODO Check for XSS
                page = pageTraverser.traverse(page, traversal);
                traversal = traversal.getNextTraversal();
            }
        }

        //TODO Visit all routes again checking for XSS
    }
}
