package org.xssfinder.reporting;

import org.xssfinder.xss.XssDescriptor;

/**
 * A sighting of a successful a particular XSS attack on a particular page
 */
public class XssSighting {
    private final Object sightingPageObject;
    private final XssDescriptor xssDescriptor;

    public XssSighting(Object sightingPageObject, XssDescriptor xssDescriptor) {
        this.sightingPageObject = sightingPageObject;
        this.xssDescriptor = xssDescriptor;
    }

    public String getVulnerableClassName() {
        return xssDescriptor.getSubmitMethod().getDeclaringClass().getCanonicalName();
    }

    public String getSightingClassName() {
        return sightingPageObject.getClass().getCanonicalName();
    }

    public String getSubmitMethodName() {
        return xssDescriptor.getSubmitMethod().getName();
    }

    public String getInputIdentifier() {
        return xssDescriptor.getInputIdentifier();
    }
}
