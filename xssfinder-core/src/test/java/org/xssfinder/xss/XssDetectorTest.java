package org.xssfinder.xss;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.runner.DriverWrapper;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XssDetectorTest {
    @Mock
    private DriverWrapper mockDriverWrapper;

    @Test
    public void xssDetectorInterrogatesDriverWrapperForCurrentXssIds() {
        // given
        Set<String> expectedXssIds = ImmutableSet.of("1", "2");
        when(mockDriverWrapper.getCurrentXssIds()).thenReturn(expectedXssIds);
        XssDetector xssDetector = new XssDetector();

        // when
        Set<String> xssIds = xssDetector.getCurrentXssIds(mockDriverWrapper);

        // then
        assertThat(xssIds, is(expectedXssIds));
    }

    @Test
    public void currentXssIdsIsEmptySetIfNullIsReturnedFromDriverWrapper() {
        // given
        when(mockDriverWrapper.getCurrentXssIds()).thenReturn(null);
        XssDetector xssDetector = new XssDetector();

        // when
        Set<String> xssIds = xssDetector.getCurrentXssIds(mockDriverWrapper);

        // then
        Set<String> expectedXssIds = ImmutableSet.of();
        assertThat(xssIds, is(expectedXssIds));
    }
}