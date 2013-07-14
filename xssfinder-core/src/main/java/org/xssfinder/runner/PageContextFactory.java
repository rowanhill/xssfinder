package org.xssfinder.runner;

import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.routing.Route;

class PageContextFactory {
    public PageContext createContext(ExecutorWrapper executor, Route route, XssJournal xssJournal) {
        return new PageContext(executor, xssJournal, route.getPageTraversal(), route.getRootPageDescriptor());
    }
}
