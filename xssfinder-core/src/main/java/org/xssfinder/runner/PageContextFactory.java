package org.xssfinder.runner;

import org.xssfinder.reporting.XssJournal;
import org.xssfinder.routing.Route;

class PageContextFactory {
    private final PageTraverser pageTraverser;
    private final PageInstantiator pageInstantiator;

    public PageContextFactory(PageTraverser pageTraverser, PageInstantiator pageInstantiator) {
        this.pageTraverser = pageTraverser;
        this.pageInstantiator = pageInstantiator;
    }

    public PageContext createContext(DriverWrapper driverWrapper, Route route, XssJournal xssJournal) {
        Object page = pageInstantiator.instantiatePage(route.getRootPageClass());
        return new PageContext(pageTraverser, page, driverWrapper, xssJournal, route.getPageTraversal(), route.getRootPageDescriptor());
    }
}
