package org.xssfinder.runner;

public interface PageInstantiator {
    <T> T instantiatePage(Class<T> pageClass);
}
