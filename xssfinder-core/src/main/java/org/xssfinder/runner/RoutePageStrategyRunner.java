package org.xssfinder.runner;

import org.xssfinder.routing.Route;
import org.xssfinder.reporting.XssJournal;

import java.util.List;

public class RoutePageStrategyRunner {
    private final DriverWrapper driverWrapper;
    private final PageContextFactory contextFactory;

    public RoutePageStrategyRunner(DriverWrapper driverWrapper, PageContextFactory contextFactory) {
        this.driverWrapper = driverWrapper;
        this.contextFactory = contextFactory;
    }

    public void run(List<Route> routes, List<PageStrategy> pageStrategies, XssJournal xssJournal) {
        for (Route route : routes) {
            driverWrapper.visit(route.getUrl());

            PageContext pageContext = contextFactory.createContext(driverWrapper, route);

            while (pageContext.hasNextContext()) {
                executePageStrategies(pageStrategies, pageContext, xssJournal);
                pageContext = pageContext.getNextContext();
            }
            executePageStrategies(pageStrategies, pageContext, xssJournal);
        }
    }

    private void executePageStrategies(List<PageStrategy> pageStrategies, PageContext context, XssJournal xssJournal) {
        for (PageStrategy pageStrategy : pageStrategies) {
            pageStrategy.processPage(context, xssJournal);
        }
    }
}
