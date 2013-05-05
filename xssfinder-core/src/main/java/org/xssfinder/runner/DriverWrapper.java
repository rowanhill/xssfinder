package org.xssfinder.runner;

public interface DriverWrapper {
    PageInstantiator getPageInstantiator();

    void visit(String url);
}
