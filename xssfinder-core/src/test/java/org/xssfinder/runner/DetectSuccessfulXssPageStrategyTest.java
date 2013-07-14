package org.xssfinder.runner;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.xss.XssDetector;
import org.xssfinder.reporting.XssJournal;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DetectSuccessfulXssPageStrategyTest {
    @Mock
    private PageContext mockPageContext;
    @Mock
    private XssDetector mockXssDetector;
    @Mock
    private XssJournal mockXssJournal;

    @Test
    public void processingPageMarksAllPresentXssIdsAsSuccessfulInJournal() {
        // given
        ExecutorWrapper mockExecutor = mock(ExecutorWrapper.class);
        when(mockPageContext.getExecutor()).thenReturn(mockExecutor);
        Set<String> successfulXssIds = ImmutableSet.of("1", "2", "5");
        when(mockXssDetector.getCurrentXssIds(mockExecutor)).thenReturn(successfulXssIds);
        DetectSuccessfulXssPageStrategy strategy = new DetectSuccessfulXssPageStrategy(mockXssDetector);

        // when
        strategy.processPage(mockPageContext, mockXssJournal);

        // then
        verify(mockXssJournal).markAsSuccessful(mockPageContext, successfulXssIds);
    }
}
