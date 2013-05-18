package org.xssfinder.xss;

public class XssDescriptor {
    private final Class<?> pageClass;
    private final String inputIdentifier;

    public XssDescriptor(Class<?> pageClass, String inputIdentifier) {
        this.pageClass = pageClass;
        this.inputIdentifier = inputIdentifier;
    }

    public Class<?> getPageClass() {
        return pageClass;
    }

    public String getInputIdentifier() {
        return inputIdentifier;
    }
}
