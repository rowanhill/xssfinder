package org.xssfinder.xss;

public class XssDescriptor {
    private final Class<?> pageClass;
    private final String inputIdentifier;

    public XssDescriptor(Class<?> pageClass, String inputIdentifier) {
        //To change body of created methods use File | Settings | File Templates.
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
