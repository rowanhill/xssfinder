package org.xssfinder.runner;

import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.remote.PageDefinition;
import org.xssfinder.remote.TLifecycleEventHandlerException;
import org.xssfinder.remote.TWebInteractionException;
import org.xssfinder.reporting.RouteRunErrorContext;
import org.xssfinder.reporting.RouteRunErrorContextFactory;
import org.xssfinder.routing.Route;
import org.xssfinder.reporting.XssJournal;

import java.util.List;

class RoutePageStrategyRunner {
    private final ExecutorWrapper executor;
    private final PageContextFactory contextFactory;
    private final RouteRunErrorContextFactory errorContextFactory;

    public RoutePageStrategyRunner(
            ExecutorWrapper executor,
            PageContextFactory contextFactory,
            RouteRunErrorContextFactory errorContextFactory
    ) {
        this.executor = executor;
        this.contextFactory = contextFactory;
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
        PageContext pageContext = null;
        try {
            try {
                executor.startRoute(route.getRootPageDefinition().getIdentifier());

                pageContext = contextFactory.createContext(route);
                while (pageContext.hasNextContext()) {
                    executePageStrategies(pageStrategies, pageContext, xssJournal);
                    pageContext = pageContext.getNextContext();
                }
                executePageStrategies(pageStrategies, pageContext, xssJournal);
            } catch (Exception e) {
                e.printStackTrace();
                RouteRunErrorContext errorContext = errorContextFactory.createErrorContext(e, pageContext);
                xssJournal.addErrorContext(errorContext);
            } finally {
                invokeAfterRouteIfNeeded(pageContext, route.getRootPageDefinition());
            }
        } catch (Exception e) {
            e.getStackTrace();
            RouteRunErrorContext errorContext = errorContextFactory.createErrorContext(e, pageContext);
            xssJournal.addErrorContext(errorContext);
        }
    }

    private void executePageStrategies(List<PageStrategy> pageStrategies, PageContext context, XssJournal xssJournal)
            throws TWebInteractionException
    {
        for (PageStrategy pageStrategy : pageStrategies) {
            pageStrategy.processPage(context, xssJournal);
        }
    }

    private void invokeAfterRouteIfNeeded(PageContext pageContext, PageDefinition rootPageDefinition)
            throws TWebInteractionException, TLifecycleEventHandlerException {
        if (pageContext == null) {
            return;
        }
        executor.invokeAfterRouteHandler(rootPageDefinition.getIdentifier());
    }
}
