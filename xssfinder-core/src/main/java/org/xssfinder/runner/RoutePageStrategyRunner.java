package org.xssfinder.runner;

import org.xssfinder.routing.Route;
import org.xssfinder.reporting.XssJournal;

import java.util.List;

class RoutePageStrategyRunner {
    private final DriverWrapper driverWrapper;
    private final PageContextFactory contextFactory;
    private final LifecycleEventExecutor lifecycleEventExecutor;

    public RoutePageStrategyRunner(
            DriverWrapper driverWrapper,
            PageContextFactory contextFactory,
            LifecycleEventExecutor lifecycleEventExecutor
    ) {
        this.driverWrapper = driverWrapper;
        this.contextFactory = contextFactory;
        this.lifecycleEventExecutor = lifecycleEventExecutor;
    }

    /**
     * Run through the given routes, executing the given strategies on them, recording results in the given journal
     *
     * @param routes A list of routes to run through
     * @param pageStrategies A list of strategies to execute against each page
     * @param xssJournal A journal to record results in
     */
    public void run(List<Route> routes, List<PageStrategy> pageStrategies, XssJournal xssJournal) {
        for (Route route : routes) {
            driverWrapper.visit(route.getUrl());

            Object lifecycleHandler = route.createLifecycleHandler();

            PageContext pageContext = contextFactory.createContext(driverWrapper, route);

            while (pageContext.hasNextContext()) {
                executePageStrategies(pageStrategies, pageContext, xssJournal);
                pageContext = pageContext.getNextContext();
            }
            executePageStrategies(pageStrategies, pageContext, xssJournal);

            lifecycleEventExecutor.afterRoute(lifecycleHandler, pageContext.getPage());
        }
    }

    private void executePageStrategies(List<PageStrategy> pageStrategies, PageContext context, XssJournal xssJournal) {
        for (PageStrategy pageStrategy : pageStrategies) {
            pageStrategy.processPage(context, xssJournal);
        }
    }
}
