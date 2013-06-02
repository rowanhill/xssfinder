package org.xssfinder.runner;

import org.xssfinder.xss.XssDetector;
import org.xssfinder.reporting.XssJournal;

/**
 * Processes a page looking for successful XSS attacks and logs them in the journal
 */
public class DetectSuccessfulXssPageStrategy implements PageStrategy {
    private final XssDetector xssDetector;

    public DetectSuccessfulXssPageStrategy(XssDetector xssDetector) {
        this.xssDetector = xssDetector;
    }

    @Override
    public void processPage(PageContext pageContext, XssJournal xssJournal) {
        xssJournal.markAsSuccessful(pageContext, xssDetector.getCurrentXssIds(pageContext.getDriverWrapper()));
    }
}
