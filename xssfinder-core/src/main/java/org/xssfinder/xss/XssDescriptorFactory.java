package org.xssfinder.xss;

public class XssDescriptorFactory {
    public XssDescriptor createXssDescriptor(Object page, String inputIdentifier) {
        return new XssDescriptor(page.getClass(), inputIdentifier);
    }
}
