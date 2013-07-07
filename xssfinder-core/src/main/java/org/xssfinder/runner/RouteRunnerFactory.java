package org.xssfinder.runner;

import org.xssfinder.reflection.Instantiator;
import org.xssfinder.reporting.HtmlReportWriter;
import org.xssfinder.reporting.RouteRunErrorContextFactory;
import org.xssfinder.xss.*;

public class RouteRunnerFactory {
    private final PageTraverser pageTraverser;
    private final XssDetector xssDetector;
    private final PageAttacker pageAttacker;

    public RouteRunnerFactory() {
        Instantiator instantiator = new Instantiator();
        pageTraverser = new PageTraverser(
                new CustomTraverserInstantiator(instantiator),
                new CustomSubmitterInstantiator(instantiator),
                new LabelledXssGeneratorFactory());
        pageAttacker = new PageAttacker(new XssGenerator(new XssAttackFactory()), new XssDescriptorFactory());
        xssDetector = new XssDetector();
    }

    public RouteRunner createRouteRunner(DriverWrapper driverWrapper, String outputFile) {
        return new RouteRunner(
                createRouteStrategyRunner(driverWrapper),
                createAttackPageStrategy(),
                createDetectSuccessfulXssPageStrategy(),
                new DetectUntestedInputsPageStrategy(),
                new HtmlReportWriter(outputFile)
        );
    }

    private RoutePageStrategyRunner createRouteStrategyRunner(DriverWrapper driverWrapper) {
        return new RoutePageStrategyRunner(
                driverWrapper,
                createPageContextFactory(driverWrapper),
                new LifecycleEventExecutor(),
                new RouteRunErrorContextFactory()
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
