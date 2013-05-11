package org.xssfinder.runner;

import org.xssfinder.routing.PageTraversal;
import org.xssfinder.routing.Route;
import org.xssfinder.xss.XssDescriptor;
import org.xssfinder.xss.XssJournal;

import java.util.List;
import java.util.Map;

public class RouteRunner {
    private final PageAttacker pageAttacker;
    private final DriverWrapper driverWrapper;
    private final XssJournal xssJournal;
    private final PageInstantiator pageInstantiator;
    private final PageTraverser pageTraverser;
    private final List<Route> routes;

    public RouteRunner(
            PageAttacker pageAttacker,
            DriverWrapper driverWrapper,
            PageTraverser pageTraverser,
            XssJournal xssJournal,
            List<Route> routes
    ) {
        this.pageAttacker = pageAttacker;
        this.driverWrapper = driverWrapper;
        this.pageInstantiator = driverWrapper.getPageInstantiator();
        this.pageTraverser = pageTraverser;
        this.xssJournal = xssJournal;
        this.routes = routes;
    }

    public void run() {
        for (Route route : routes) {
            driverWrapper.visit(route.getUrl());

            Object page = pageInstantiator.instantiatePage(route.getRootPageClass());

            PageTraversal traversal = route.getPageTraversal();
            //TODO Warn of missing @SubmitAction if needed
            while (traversal != null) {
                Map<String, XssDescriptor> xssIdsToXssDescriptors =
                        pageAttacker.attackIfAboutToSubmit(page, driverWrapper, traversal);
                for (Map.Entry<String, XssDescriptor> entry : xssIdsToXssDescriptors.entrySet()) {
                    xssJournal.addXssDescriptor(entry.getKey(), entry.getValue());
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
