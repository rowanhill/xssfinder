package org.xssfinder.runner;

import java.util.Collections;
import java.util.Set;

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
