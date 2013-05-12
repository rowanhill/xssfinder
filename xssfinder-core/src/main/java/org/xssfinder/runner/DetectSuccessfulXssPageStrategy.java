package org.xssfinder.runner;

import org.xssfinder.routing.PageTraversal;
import org.xssfinder.xss.XssDetector;
import org.xssfinder.xss.XssJournal;

public class DetectSuccessfulXssPageStrategy implements PageStrategy {
    private final XssDetector xssDetector;
    private final XssJournal xssJournal;

    public DetectSuccessfulXssPageStrategy(XssDetector xssDetector, XssJournal xssJournal) {
        this.xssDetector = xssDetector;
        this.xssJournal = xssJournal;
    }

    @Override
    public void processPage(Object page, PageTraversal nextTraversal, DriverWrapper driverWrapper) {
        xssJournal.markAsSuccessful(xssDetector.getCurrentXssIds(driverWrapper));
    }
}
