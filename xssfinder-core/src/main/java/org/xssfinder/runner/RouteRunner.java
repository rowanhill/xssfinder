package org.xssfinder.runner;

import com.google.common.collect.ImmutableList;
import org.xssfinder.routing.Route;
import org.xssfinder.reporting.XssJournal;

import java.util.List;

public class RouteRunner {

    private final RoutePageStrategyRunner strategyRunner;
    private final AttackPageStrategy attackStrategy;
    private final DetectSuccessfulXssPageStrategy detectStrategy;

    public RouteRunner(
            RoutePageStrategyRunner strategyRunner,
            AttackPageStrategy attackStrategy,
            DetectSuccessfulXssPageStrategy detectStrategy
    ) {
        this.strategyRunner = strategyRunner;
        this.attackStrategy = attackStrategy;
        this.detectStrategy = detectStrategy;
    }

    public void run(List<Route> routes, XssJournal xssJournal) {
        strategyRunner.run(routes, ImmutableList.of(attackStrategy, detectStrategy), xssJournal);
        strategyRunner.run(routes, ImmutableList.of((PageStrategy)detectStrategy), xssJournal);
    }
}
