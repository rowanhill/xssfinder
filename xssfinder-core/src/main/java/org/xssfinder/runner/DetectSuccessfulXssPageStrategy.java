package org.xssfinder.runner;

import org.xssfinder.xss.XssDetector;
import org.xssfinder.reporting.XssJournal;

public class DetectSuccessfulXssPageStrategy implements PageStrategy {
    private final XssDetector xssDetector;

    public DetectSuccessfulXssPageStrategy(XssDetector xssDetector) {
        this.xssDetector = xssDetector;
    }

    @Override
    public void processPage(PageContext pageContext, XssJournal xssJournal) {
        xssJournal.markAsSuccessful(xssDetector.getCurrentXssIds(pageContext.getDriverWrapper()));
    }
}
