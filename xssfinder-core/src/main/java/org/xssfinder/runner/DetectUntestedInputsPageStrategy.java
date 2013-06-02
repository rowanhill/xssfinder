package org.xssfinder.runner;

import org.xssfinder.reporting.XssJournal;
import org.xssfinder.routing.PageDescriptor;

/**
 * Detects page objects with insufficient @SubmitActions and logs in the journal
 */
public class DetectUntestedInputsPageStrategy implements PageStrategy {
    @Override
    public void processPage(PageContext pageContext, XssJournal xssJournal) {
        DriverWrapper driverWrapper = pageContext.getDriverWrapper();
        int seenForms = driverWrapper.getFormCount();

        PageDescriptor pageDescriptor = pageContext.getPageDescriptor();
        int submitMethods = pageDescriptor.getSubmitMethods().size();

        if (seenForms > submitMethods) {
            xssJournal.addPageClassWithUntestedInputs(pageDescriptor.getPageClass());
        }
    }
}
