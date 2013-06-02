package org.xssfinder.runner;

/**
 * Interface for factories that create page objects, usually by supplying some kind of web driver
 */
public interface PageInstantiator {
    <T> T instantiatePage(Class<T> pageClass);
}
