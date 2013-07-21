package org.xssfinder.runner;

import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.reporting.HtmlReportWriter;
import org.xssfinder.reporting.RouteRunErrorContextFactory;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.reporting.XssSightingFactory;
import org.xssfinder.xss.*;

public class RouteRunnerFactory {
    private final XssDetector xssDetector;

    public RouteRunnerFactory() {
        xssDetector = new XssDetector();
    }

    public RouteRunner createRouteRunner(ExecutorWrapper executor, String outputFile) {
        XssJournal xssJournal = new XssJournal(new XssSightingFactory());
        return new RouteRunner(
                createRouteStrategyRunner(executor, xssJournal),
                createAttackPageStrategy(executor),
                createDetectSuccessfulXssPageStrategy(),
                new DetectUntestedInputsPageStrategy(),
                new HtmlReportWriter(outputFile),
                xssJournal
        );
    }

    private RoutePageStrategyRunner createRouteStrategyRunner(ExecutorWrapper executor, XssJournal xssJournal) {
        return new RoutePageStrategyRunner(
                executor,
                createPageContextFactory(executor, xssJournal),
                new RouteRunErrorContextFactory()
        );
    }

    private PageContextFactory createPageContextFactory(ExecutorWrapper executor, XssJournal xssJournal) {
        return new PageContextFactory(executor, xssJournal, new XssDescriptorFactory());
    }

    private AttackPageStrategy createAttackPageStrategy(ExecutorWrapper executor) {
        return new AttackPageStrategy(new PageAttacker(executor, new XssDescriptorFactory()));
    }

    private DetectSuccessfulXssPageStrategy createDetectSuccessfulXssPageStrategy() {
        return new DetectSuccessfulXssPageStrategy(xssDetector);
    }
}
