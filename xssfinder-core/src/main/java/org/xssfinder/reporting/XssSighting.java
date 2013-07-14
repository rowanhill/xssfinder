package org.xssfinder.reporting;

import org.xssfinder.remote.PageDefinition;
import org.xssfinder.xss.XssDescriptor;

/**
 * A sighting of a successful a particular XSS attack on a particular page
 */
public class XssSighting {
    private final PageDefinition sightingPageDefinition;
    private final XssDescriptor xssDescriptor;

    public XssSighting(PageDefinition sightingPageDefinition, XssDescriptor xssDescriptor) {
        this.sightingPageDefinition = sightingPageDefinition;
        this.xssDescriptor = xssDescriptor;
    }

    public String getVulnerableClassName() {
        return xssDescriptor.getSubmitMethod().getOwningType().getIdentifier();
    }

    public String getSightingClassName() {
        return sightingPageDefinition.getIdentifier();
    }

    public String getSubmitMethodName() {
        return xssDescriptor.getSubmitMethod().getIdentifier();
    }

    public String getInputIdentifier() {
        return xssDescriptor.getInputIdentifier();
    }
}
