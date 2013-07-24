package org.xssfinder.runner;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.reporting.HtmlReportWriter;
import org.xssfinder.routing.Route;
import org.xssfinder.reporting.XssJournal;

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
    @Mock
    private DetectUntestedInputsPageStrategy mockWarnStrategy;
    @Mock
    private HtmlReportWriter mockReportWriter;
    @Mock
    private XssJournal mockXssJournal;

    private final List<Route> routes = new ArrayList<Route>();

    @Test
    public void runAttacksAllPagesThenVerifiesAllPagesThenWritesReport() throws Exception {
        // given
        RouteRunner runner = new RouteRunner(mockStrategyRunner, mockAttackStrategy, mockDetectStrategy, mockWarnStrategy, mockReportWriter, mockXssJournal);

        // when
        runner.run(routes);

        // then
        List<PageStrategy> attackPhaseStrategies = ImmutableList.of(mockAttackStrategy, mockDetectStrategy, mockWarnStrategy);
        List<PageStrategy> detectPhaseStrategies = ImmutableList.of((PageStrategy)mockDetectStrategy);
        InOrder inOrder = inOrder(mockStrategyRunner, mockReportWriter);
        inOrder.verify(mockStrategyRunner).run(routes, attackPhaseStrategies, mockXssJournal);
        inOrder.verify(mockStrategyRunner).run(routes, detectPhaseStrategies, mockXssJournal);
        inOrder.verify(mockReportWriter).write(mockXssJournal);
        inOrder.verifyNoMoreInteractions();
    }
}
