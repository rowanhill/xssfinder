package org.xssfinder.runner;

import org.xssfinder.routing.PageTraversal;
import org.xssfinder.routing.Route;
import org.xssfinder.xss.XssDescriptor;
import org.xssfinder.xss.XssGenerator;
import org.xssfinder.xss.XssJournal;

import java.util.List;
import java.util.Map;

public class RouteRunner {
    private final DriverWrapper driverWrapper;
    private final XssJournal xssJournal;
    private final XssGenerator xssGenerator;
    private final PageInstantiator pageInstantiator;
    private final PageTraverser pageTraverser;
    private final List<Route> routes;

    public RouteRunner(
            DriverWrapper driverWrapper,
            PageTraverser pageTraverser,
            XssGenerator xssGenerator,
            XssJournal xssJournal,
            List<Route> routes
    ) {
        this.driverWrapper = driverWrapper;
        this.pageInstantiator = driverWrapper.getPageInstantiator();
        this.pageTraverser = pageTraverser;
        this.xssGenerator = xssGenerator;
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
                if (traversal.isSubmit()) {
                    Map<String,String> inputsToXssIds = driverWrapper.putXssAttackStringsInInputs(xssGenerator);
                    for (Map.Entry<String,String> entry : inputsToXssIds.entrySet()) {
                        XssDescriptor descriptor = new XssDescriptor(page.getClass(), entry.getKey());
                        xssJournal.addXssDescriptor(entry.getValue(), descriptor);
                    }
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
