package org.xssfinder.xss;

import java.lang.reflect.Method;

/**
 * Description of a particular XSS attack (with a unique ID) and how it was submitted
 */
public class XssDescriptor {
    private final Method submitMethod;
    private final String inputIdentifier;

    public XssDescriptor(Method submitMethod, String inputIdentifier) {
        this.submitMethod = submitMethod;
        this.inputIdentifier = inputIdentifier;
    }

    public Method getSubmitMethod() {
        return submitMethod;
    }

    public String getInputIdentifier() {
        return inputIdentifier;
    }
}
