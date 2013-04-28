package org.xssfinder.runner;

public interface PageInstantiator {
    <T> Object instantiatePage(Class<T> pageClass);
}
