package org.xssfinder.runner;

import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.routing.Route;

class PageContextFactory {
    private final ExecutorWrapper executor;
    private final XssJournal xssJournal;

    PageContextFactory(ExecutorWrapper executor, XssJournal xssJournal) {
        this.executor = executor;
        this.xssJournal = xssJournal;
    }

    public PageContext createContext(Route route) {
        return new PageContext(executor, xssJournal, route.getPageTraversal(), route.getRootPageDescriptor());
    }
}
