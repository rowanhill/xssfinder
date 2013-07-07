package org.xssfinder.runner;

import org.xssfinder.reporting.RouteRunErrorContext;
import org.xssfinder.reporting.RouteRunErrorContextFactory;
import org.xssfinder.routing.Route;
import org.xssfinder.reporting.XssJournal;

import java.util.List;

class RoutePageStrategyRunner {
    private final DriverWrapper driverWrapper;
    private final PageContextFactory contextFactory;
    private final LifecycleEventExecutor lifecycleEventExecutor;
    private final RouteRunErrorContextFactory errorContextFactory;

    public RoutePageStrategyRunner(
            DriverWrapper driverWrapper,
            PageContextFactory contextFactory,
            LifecycleEventExecutor lifecycleEventExecutor,
            RouteRunErrorContextFactory errorContextFactory
    ) {
        this.driverWrapper = driverWrapper;
        this.contextFactory = contextFactory;
        this.lifecycleEventExecutor = lifecycleEventExecutor;
        this.errorContextFactory = errorContextFactory;
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
            runRoute(route, xssJournal, pageStrategies);
        }
    }

    private void runRoute(Route route, XssJournal xssJournal, List<PageStrategy> pageStrategies) {
        Object lifecycleHandler = null;
        PageContext pageContext = null;
        try {
            driverWrapper.visit(route.getUrl());

            lifecycleHandler = route.createLifecycleHandler();

            pageContext = contextFactory.createContext(driverWrapper, route, xssJournal);
            while (pageContext.hasNextContext()) {
                executePageStrategies(pageStrategies, pageContext, xssJournal);
                pageContext = pageContext.getNextContext();
            }
            executePageStrategies(pageStrategies, pageContext, xssJournal);
        } catch (Exception e) {
            RouteRunErrorContext errorContext = errorContextFactory.createErrorContext(e, pageContext);
            xssJournal.addErrorContext(errorContext);
        } finally {
            handleRouteError(lifecycleHandler, pageContext);
        }
    }

    private void executePageStrategies(List<PageStrategy> pageStrategies, PageContext context, XssJournal xssJournal) {
        for (PageStrategy pageStrategy : pageStrategies) {
            pageStrategy.processPage(context, xssJournal);
        }
    }

    private void handleRouteError(Object lifecycleHandler, PageContext pageContext) {
        if (pageContext == null) {
            return;
        }
        try {
            lifecycleEventExecutor.afterRoute(lifecycleHandler, pageContext.getPage());
        } catch (Exception e) {
            // Do nothing... for now
        }
    }
}
