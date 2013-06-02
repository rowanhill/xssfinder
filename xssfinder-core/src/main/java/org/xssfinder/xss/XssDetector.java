package org.xssfinder.xss;

import org.xssfinder.runner.DriverWrapper;

import java.util.Collections;
import java.util.Set;

/**
 * Looks for successful attacks on the current page
 */
public class XssDetector {
    public Set<String> getCurrentXssIds(DriverWrapper driverWrapper) {
        Set<String> currentXssIds = driverWrapper.getCurrentXssIds();
        if (currentXssIds == null) {
            return Collections.emptySet();
        } else {
            return currentXssIds;
        }
    }
}
