package org.xssfinder.runner;

import org.xssfinder.routing.PageTraversal;

public interface PageStrategy {
    void processPage(Object page, PageTraversal nextTraversal, DriverWrapper driverWrapper);
}
