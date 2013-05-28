package org.xssfinder.runner;

import org.xssfinder.routing.PageDescriptor;
import org.xssfinder.routing.PageTraversal;

public class PageContext {
    private final PageTraverser pageTraverser;
    private final Object page;
    private final DriverWrapper driverWrapper;
    private final PageTraversal pageTraversal;
    private PageDescriptor pageDescriptor;

    public PageContext(
            PageTraverser pageTraverser,
            Object page,
            DriverWrapper driverWrapper,
            PageTraversal pageTraversal,
            PageDescriptor pageDescriptor
    ) {
        this.pageTraverser = pageTraverser;
        this.page = page;
        this.driverWrapper = driverWrapper;
        this.pageTraversal = pageTraversal;
        this.pageDescriptor = pageDescriptor;
    }

    public boolean hasNextContext() {
        return pageTraversal != null;
    }

    public PageContext getNextContext() {
        if (!hasNextContext()) {
            throw new IllegalStateException();
        }
        Object nextPage = pageTraverser.traverse(page, pageTraversal);
        return new PageContext(pageTraverser, nextPage, driverWrapper, pageTraversal.getNextTraversal(), pageTraversal.getResultingPageDescriptor());
    }

    public Object getPage() {
        return page;
    }

    public DriverWrapper getDriverWrapper() {
        return driverWrapper;
    }

    public PageTraversal getPageTraversal() {
        return pageTraversal;
    }

    public PageDescriptor getPageDescriptor() {
        return pageDescriptor;
    }
}
