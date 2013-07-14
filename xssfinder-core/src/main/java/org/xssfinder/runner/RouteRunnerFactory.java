package org.xssfinder.runner;

import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.reporting.HtmlReportWriter;
import org.xssfinder.reporting.RouteRunErrorContextFactory;
import org.xssfinder.xss.*;

public class RouteRunnerFactory {
    private final XssDetector xssDetector;

    public RouteRunnerFactory() {
        xssDetector = new XssDetector();
    }

    public RouteRunner createRouteRunner(ExecutorWrapper executor, String outputFile) {
        return new RouteRunner(
                createRouteStrategyRunner(executor),
                createAttackPageStrategy(executor),
                createDetectSuccessfulXssPageStrategy(),
                new DetectUntestedInputsPageStrategy(),
                new HtmlReportWriter(outputFile)
        );
    }

    private RoutePageStrategyRunner createRouteStrategyRunner(ExecutorWrapper executor) {
        return new RoutePageStrategyRunner(
                executor,
                createPageContextFactory(),
                new LifecycleEventExecutor(),
                new RouteRunErrorContextFactory()
        );
    }

    private PageContextFactory createPageContextFactory() {
        return new PageContextFactory();
    }

    private AttackPageStrategy createAttackPageStrategy(ExecutorWrapper executor) {
        return new AttackPageStrategy(new PageAttacker(executor, new XssDescriptorFactory()));
    }

    private DetectSuccessfulXssPageStrategy createDetectSuccessfulXssPageStrategy() {
        return new DetectSuccessfulXssPageStrategy(xssDetector);
    }
}
