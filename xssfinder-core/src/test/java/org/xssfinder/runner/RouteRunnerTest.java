package org.xssfinder.runner;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.routing.Route;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.inOrder;

@RunWith(MockitoJUnitRunner.class)
public class RouteRunnerTest {
    @Mock
    private RoutePageStrategyRunner mockStrategyRunner;
    @Mock
    private AttackPageStrategy mockAttackStrategy;
    @Mock
    private DetectSuccessfulXssPageStrategy mockDetectStrategy;

    private List<Route> routes = new ArrayList<Route>();

    @Test
    public void runAttacksAllPagesThenVerifiesAllPages() {
        // given
        RouteRunner runner = new RouteRunner(mockStrategyRunner, mockAttackStrategy, mockDetectStrategy);

        // when
        runner.run(routes);

        // then
        List<PageStrategy> attackPhaseStrategies = ImmutableList.of(mockAttackStrategy, mockDetectStrategy);
        List<PageStrategy> detectPhaseStrategies = ImmutableList.of((PageStrategy)mockDetectStrategy);
        InOrder inOrder = inOrder(mockStrategyRunner);
        inOrder.verify(mockStrategyRunner).run(routes, attackPhaseStrategies);
        inOrder.verify(mockStrategyRunner).run(routes, detectPhaseStrategies);
        inOrder.verifyNoMoreInteractions();
    }
}
