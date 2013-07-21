package org.xssfinder.runner;

import org.xssfinder.remote.TWebInteractionException;
import org.xssfinder.reporting.XssJournal;

/**
 * Interface for strategies used by RoutePageStrategyRunner to process a page when traversing a route
 *
 * @see RoutePageStrategyRunner
 */
interface PageStrategy {
    void processPage(PageContext pageContext, XssJournal xssJournal) throws TWebInteractionException;
}
