package org.xssfinder.runner;

import org.xssfinder.xss.XssGenerator;

public interface DriverWrapper {
    PageInstantiator getPageInstantiator();

    void visit(String url);

    void putXssAttackStringsInInputs(XssGenerator xssGenerator);
}
