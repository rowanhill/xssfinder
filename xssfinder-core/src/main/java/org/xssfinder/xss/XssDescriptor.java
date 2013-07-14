package org.xssfinder.xss;

import org.xssfinder.remote.MethodDefinition;

/**
 * Description of a particular XSS attack (with a unique ID) and how it was submitted
 */
public class XssDescriptor {
    private final MethodDefinition submitMethod;
    private final String inputIdentifier;

    public XssDescriptor(MethodDefinition submitMethod, String inputIdentifier) {
        this.submitMethod = submitMethod;
        this.inputIdentifier = inputIdentifier;
    }

    public MethodDefinition getSubmitMethod() {
        return submitMethod;
    }

    public String getInputIdentifier() {
        return inputIdentifier;
    }
}
