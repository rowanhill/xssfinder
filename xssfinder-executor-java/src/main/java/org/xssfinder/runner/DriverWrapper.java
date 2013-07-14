package org.xssfinder.runner;

import org.xssfinder.xss.XssGenerator;

import java.util.Map;
import java.util.Set;

/**
 * Interface that adaptors wrapping different website renderers (like WebDriver) must implement
 */
public interface DriverWrapper {
    /**
     * @return A PageInstantiator that can create pages driven by the wrapped driver
     */
    PageInstantiator getPageInstantiator();

    /**
     * Navigate the driver to given URL
     *
     * @param url The URL to visit
     */
    void visit(String url);

    /**
     * Put XSS attacks from the given XssGenerator into all available inputs
     *
     * @param xssGenerator XssGenerator to produce XSS attacks
     * @return A map of input identifiers -> attack identifiers
     */
    Map<String, String> putXssAttackStringsInInputs(XssGenerator xssGenerator);

    /**
     * @return The set of currently XSS attack identifiers observable on the current page
     */
    Set<String> getCurrentXssIds();

    /**
     * @return The number of forms observable on the current page
     */
    int getFormCount();
}
