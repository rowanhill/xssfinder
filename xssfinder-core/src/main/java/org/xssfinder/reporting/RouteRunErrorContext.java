package org.xssfinder.reporting;

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

    public String getPageIdentifier() {
        return pageContext == null ? null : pageContext.getPageDescriptor().getPageDefinition().getIdentifier();
    }

    public String getPageTraversalMethodString() {
        return pageContext == null ? null : pageContext.getPageTraversal().getMethod().toString();
    }

    public String getTraversalModeName() {
        return pageContext == null ? null : pageContext.getPageTraversal().getTraversalMode().getDescription();
    }
}
