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

    public String getPageIdentifier() {
        return pageContext == null ? null : pageContext.getPageDescriptor().getPageDefinition().getIdentifier();
    }

    public String getPageTraversalMethodString() {
        PageTraversal traversal = pageContext == null ? null : pageContext.getPageTraversal();
        return traversal == null ? null : traversal.getMethod().getIdentifier();
    }

    public String getTraversalModeName() {
        PageTraversal traversal = pageContext == null ? null : pageContext.getPageTraversal();
        return traversal == null ? null : traversal.getTraversalMode().getDescription();
    }
}
