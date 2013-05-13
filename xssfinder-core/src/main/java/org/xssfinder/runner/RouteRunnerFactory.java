package org.xssfinder.runner;

import org.xssfinder.xss.*;

public class RouteRunnerFactory {
    private final PageTraverser pageTraverser;
    private final PageAttacker pageAttacker;
    private final XssDetector xssDetector;

    public RouteRunnerFactory() {
        pageTraverser = new PageTraverser();
        pageAttacker = new PageAttacker(new XssGenerator(new XssAttackFactory()), new XssDescriptorFactory());
        xssDetector = new XssDetector();
    }

    public RouteRunner createRouteRunner(DriverWrapper driverWrapper, XssJournal xssJournal) {
        return new RouteRunner(
                createRouteStrategyRunner(driverWrapper),
                createAttackPageStrategy(xssJournal),
                createDetectSuccessfulXssPageStrategy(xssJournal)
        );
    }

    private RoutePageStrategyRunner createRouteStrategyRunner(DriverWrapper driverWrapper) {
        return new RoutePageStrategyRunner(
                driverWrapper,
                createPageContextFactory(driverWrapper)
        );
    }

    private PageContextFactory createPageContextFactory(DriverWrapper driverWrapper) {
        return new PageContextFactory(
                    pageTraverser,
                    driverWrapper.getPageInstantiator()
            );
    }

    private AttackPageStrategy createAttackPageStrategy(XssJournal xssJournal) {
        return new AttackPageStrategy(pageAttacker, xssJournal);
    }

    private DetectSuccessfulXssPageStrategy createDetectSuccessfulXssPageStrategy(XssJournal xssJournal) {
        return new DetectSuccessfulXssPageStrategy(xssDetector, xssJournal);
    }
}
