package org.xssfinder.runner;

import com.google.common.collect.ImmutableList;
import org.xssfinder.routing.Route;

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

    public void run(List<Route> routes) {
        strategyRunner.run(routes, ImmutableList.of(attackStrategy, detectStrategy));
        strategyRunner.run(routes, ImmutableList.of((PageStrategy)detectStrategy));
    }
}
