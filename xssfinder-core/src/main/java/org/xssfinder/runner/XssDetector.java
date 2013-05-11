package org.xssfinder.runner;

import java.util.Set;

public class XssDetector {
    public Set<String> getCurrentXssIds(DriverWrapper driverWrapper) {
        return driverWrapper.getCurrentXssIds();
    }
}
