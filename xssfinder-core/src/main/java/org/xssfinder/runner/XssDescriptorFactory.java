package org.xssfinder.runner;

import org.xssfinder.xss.XssDescriptor;

public class XssDescriptorFactory {
    public XssDescriptor createXssDescriptor(Object page, String inputIdentifier) {
        return new XssDescriptor(page.getClass(), inputIdentifier);
    }
}
