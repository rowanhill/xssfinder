package org.xssfinder.runner;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.xss.XssDetector;
import org.xssfinder.xss.XssJournal;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DetectSuccessfulXssPageStrategyTest {
    @Mock
    private XssDetector mockXssDetector;
    @Mock
    private XssJournal mockXssJournal;

    @Test
    public void processingPageMarksAllPresentXssIdsAsSuccessfulInJournal() {
        // given
        Object page = new Object();
        PageTraversal mockNextTraversal = mock(PageTraversal.class);
        DriverWrapper mockDriverWrapper = mock(DriverWrapper.class);
        Set<String> successfulXssIds = ImmutableSet.of("1", "2", "5");
        when(mockXssDetector.getCurrentXssIds(mockDriverWrapper)).thenReturn(successfulXssIds);
        DetectSuccessfulXssPageStrategy strategy = new DetectSuccessfulXssPageStrategy(mockXssDetector, mockXssJournal);

        // when
        strategy.processPage(page, mockNextTraversal, mockDriverWrapper);

        // then
        verify(mockXssJournal).markAsSuccessful(successfulXssIds);
    }
}
