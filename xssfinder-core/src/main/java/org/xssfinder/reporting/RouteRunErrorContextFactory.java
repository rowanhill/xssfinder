package org.xssfinder.reporting;

import org.xssfinder.runner.PageContext;

public class RouteRunErrorContextFactory {

    public RouteRunErrorContext createErrorContext(Exception exception, PageContext pageContext) {
        return new RouteRunErrorContext(exception, pageContext);
    }
}
