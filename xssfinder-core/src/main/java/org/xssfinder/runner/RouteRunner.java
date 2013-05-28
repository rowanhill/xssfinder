package org.xssfinder.runner;

import com.google.common.collect.ImmutableList;
import org.xssfinder.reporting.HtmlReportWriter;
import org.xssfinder.routing.Route;
import org.xssfinder.reporting.XssJournal;

import java.io.IOException;
import java.util.List;

public class RouteRunner {

    private final RoutePageStrategyRunner strategyRunner;
    private final AttackPageStrategy attackStrategy;
    private final DetectSuccessfulXssPageStrategy detectStrategy;
    private final DetectUntestedInputsPageStrategy warnStrategy;
    private final HtmlReportWriter reportWriter;

    public RouteRunner(
            RoutePageStrategyRunner strategyRunner,
            AttackPageStrategy attackStrategy,
            DetectSuccessfulXssPageStrategy detectStrategy,
            DetectUntestedInputsPageStrategy warnStrategy,
            HtmlReportWriter reportWriter
    ) {
        this.strategyRunner = strategyRunner;
        this.attackStrategy = attackStrategy;
        this.detectStrategy = detectStrategy;
        this.warnStrategy = warnStrategy;
        this.reportWriter = reportWriter;
    }

    public void run(List<Route> routes, XssJournal xssJournal) throws IOException {
        strategyRunner.run(routes, ImmutableList.of(attackStrategy, detectStrategy, warnStrategy), xssJournal);
        strategyRunner.run(routes, ImmutableList.of((PageStrategy)detectStrategy), xssJournal);
        reportWriter.write(xssJournal);
    }
}
