package org.xssfinder.runner;

import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.routing.Route;
import org.xssfinder.xss.XssDescriptorFactory;

class PageContextFactory {
    private final ExecutorWrapper executor;
    private final XssJournal xssJournal;
    private final XssDescriptorFactory xssDescriptorFactory;

    PageContextFactory(ExecutorWrapper executor, XssJournal xssJournal, XssDescriptorFactory xssDescriptorFactory) {
        this.executor = executor;
        this.xssJournal = xssJournal;
        this.xssDescriptorFactory = xssDescriptorFactory;
    }

    public PageContext createContext(Route route) {
        return new PageContext(executor, xssJournal, xssDescriptorFactory, route.getPageTraversal(), route.getRootPageDescriptor());
    }
}
