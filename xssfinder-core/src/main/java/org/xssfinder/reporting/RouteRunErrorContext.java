package org.xssfinder.reporting;

import org.xssfinder.routing.PageTraversal;
import org.xssfinder.runner.PageContext;

import java.io.PrintWriter;

public class RouteRunErrorContext {

    private final Exception exception;
    private final PageContext pageContext;

    public RouteRunErrorContext(Exception exception, PageContext pageContext) {
        this.exception = exception;
        this.pageContext = pageContext;
    }

    public String getExceptionMessage() {
        return exception.getMessage();
    }

    public void printStackTrace(PrintWriter printWriter) {
        exception.printStackTrace(printWriter);
    }

    public String getPageClassName() {
        return pageContext.getPageDescriptor().getPageClass().getCanonicalName();
    }

    public String getPageTraversalMethodString() {
        return pageContext.getPageTraversal().getMethod().toString();
    }

    public String getTraversalModeName() {
        return pageContext.getPageTraversal().getTraversalMode().getDescription();
    }
}
