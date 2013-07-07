package org.xssfinder.runner;

import org.xssfinder.reporting.XssJournal;
import org.xssfinder.routing.PageDescriptor;
import org.xssfinder.routing.PageTraversal;

/**
 * Describes the current page and associated context whilst traversing through a route
 */
public class PageContext {
    private final PageTraverser pageTraverser;
    private final Object page;
    private final DriverWrapper driverWrapper;
    private final XssJournal xssJournal;
    private final PageTraversal pageTraversal;
    private PageDescriptor pageDescriptor;

    public PageContext(
            PageTraverser pageTraverser,
            Object page,
            DriverWrapper driverWrapper,
            XssJournal xssJournal,
            PageTraversal pageTraversal,
            PageDescriptor pageDescriptor
    ) {
        this.pageTraverser = pageTraverser;
        this.page = page;
        this.driverWrapper = driverWrapper;
        this.xssJournal = xssJournal;
        this.pageTraversal = pageTraversal;
        this.pageDescriptor = pageDescriptor;
    }

    /**
     * @return True if it is possible to traverse to the next context
     * @see org.xssfinder.runner.PageContext#getNextContext()
     */
    public boolean hasNextContext() {
        return pageTraversal != null;
    }

    /**
     * @return A PageContext created by traversing to the next page in the route
     * @see org.xssfinder.runner.PageContext#hasNextContext()
     */
    public PageContext getNextContext() {
        if (!hasNextContext()) {
            throw new IllegalStateException();
        }
        Object nextPage = pageTraverser.traverse(page, pageTraversal, xssJournal);
        return new PageContext(pageTraverser, nextPage, driverWrapper, xssJournal, pageTraversal.getNextTraversal(), pageTraversal.getResultingPageDescriptor());
    }

    /**
     * @return The current page object
     */
    public Object getPage() {
        return page;
    }

    /**
     * @return The DriverWrapper used by the page objects to interact with the web site under test
     */
    public DriverWrapper getDriverWrapper() {
        return driverWrapper;
    }

    /**
     * @return The next page traversal to be taken, or null if none
     */
    public PageTraversal getPageTraversal() {
        return pageTraversal;
    }

    /**
     * @return The page descriptor of the current page
     */
    public PageDescriptor getPageDescriptor() {
        return pageDescriptor;
    }
}
