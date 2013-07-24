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
    private final XssJournal xssJournal;

    public RouteRunner(
            RoutePageStrategyRunner strategyRunner,
            AttackPageStrategy attackStrategy,
            DetectSuccessfulXssPageStrategy detectStrategy,
            DetectUntestedInputsPageStrategy warnStrategy,
            HtmlReportWriter reportWriter,
            XssJournal xssJournal
    ) {
        this.strategyRunner = strategyRunner;
        this.attackStrategy = attackStrategy;
        this.detectStrategy = detectStrategy;
        this.warnStrategy = warnStrategy;
        this.reportWriter = reportWriter;
        this.xssJournal = xssJournal;
    }

    /**
     * Run through the given routes, first attacking them, and then just checking for successful attacks. Writes out
     * the results report when complete.
     *
     * @param routes A list of routes to run through
     * @throws IOException
     */
    public XssJournal run(List<Route> routes) throws IOException {
        strategyRunner.run(routes, ImmutableList.of(attackStrategy, detectStrategy, warnStrategy), xssJournal);
        strategyRunner.run(routes, ImmutableList.of((PageStrategy)detectStrategy), xssJournal);
        reportWriter.write(xssJournal);
        return xssJournal;
    }
}
