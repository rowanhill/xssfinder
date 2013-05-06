package org.xssfinder.runner;

import org.xssfinder.xss.XssGenerator;

import java.util.Map;
import java.util.Set;

public interface DriverWrapper {
    PageInstantiator getPageInstantiator();

    void visit(String url);

    Map<String, String> putXssAttackStringsInInputs(XssGenerator xssGenerator);
}
