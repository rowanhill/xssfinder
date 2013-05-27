package org.xssfinder.runner;

import org.xssfinder.reflection.Instantiator;
import org.xssfinder.reporting.HtmlReportWriter;
import org.xssfinder.xss.*;

public class RouteRunnerFactory {
    private final PageTraverser pageTraverser;
    private XssDetector xssDetector;
    private PageAttacker pageAttacker;

    public RouteRunnerFactory() {
        pageTraverser = new PageTraverser(new CustomTraverserInstantiator(new Instantiator()));
        pageAttacker = new PageAttacker(new XssGenerator(new XssAttackFactory()), new XssDescriptorFactory());
        xssDetector = new XssDetector();
    }

    public RouteRunner createRouteRunner(DriverWrapper driverWrapper, String outputFile) {
        return new RouteRunner(
                createRouteStrategyRunner(driverWrapper),
                createAttackPageStrategy(),
                createDetectSuccessfulXssPageStrategy(),
                new HtmlReportWriter(outputFile)
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

    private AttackPageStrategy createAttackPageStrategy() {
        return new AttackPageStrategy(pageAttacker);
    }

    private DetectSuccessfulXssPageStrategy createDetectSuccessfulXssPageStrategy() {
        return new DetectSuccessfulXssPageStrategy(xssDetector);
    }
}
