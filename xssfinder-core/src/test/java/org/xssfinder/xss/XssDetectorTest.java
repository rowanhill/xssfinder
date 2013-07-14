package org.xssfinder.xss;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.ExecutorWrapper;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XssDetectorTest {
    @Mock
    private ExecutorWrapper mockExecutor;

    @Test
    public void xssDetectorInterrogatesDriverWrapperForCurrentXssIds() {
        // given
        Set<String> expectedXssIds = ImmutableSet.of("1", "2");
        when(mockExecutor.getCurrentXssIds()).thenReturn(expectedXssIds);
        XssDetector xssDetector = new XssDetector();

        // when
        Set<String> xssIds = xssDetector.getCurrentXssIds(mockExecutor);

        // then
        assertThat(xssIds, is(expectedXssIds));
    }

    @Test
    public void currentXssIdsIsEmptySetIfNullIsReturnedFromDriverWrapper() {
        // given
        when(mockExecutor.getCurrentXssIds()).thenReturn(null);
        XssDetector xssDetector = new XssDetector();

        // when
        Set<String> xssIds = xssDetector.getCurrentXssIds(mockExecutor);

        // then
        Set<String> expectedXssIds = ImmutableSet.of();
        assertThat(xssIds, is(expectedXssIds));
    }
}
