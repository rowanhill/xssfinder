package org.xssfinder.runner;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.xss.XssDescriptor;
import org.xssfinder.xss.XssJournal;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AttackPageStrategyTest {
    @Mock
    private PageAttacker mockPageAttacker;
    @Mock
    private XssJournal mockXssJournal;

    @Test
    public void processingPageAddsAttackDescriptorsToJournal() {
        // given
        Object page = new Object();
        PageTraversal mockNextTraversal = mock(PageTraversal.class);
        DriverWrapper mockDriverWrapper = mock(DriverWrapper.class);
        XssDescriptor mockXssDescriptor = mock(XssDescriptor.class);
        Map<String, XssDescriptor> descriptorsById = ImmutableMap.of(
                "1", mockXssDescriptor
        );
        when(mockPageAttacker.attackIfAboutToSubmit(page, mockDriverWrapper, mockNextTraversal))
                .thenReturn(descriptorsById);

        AttackPageStrategy strategy = new AttackPageStrategy(mockPageAttacker, mockXssJournal);

        // when
        strategy.processPage(page, mockNextTraversal, mockDriverWrapper);

        // then
        verify(mockXssJournal).addXssDescriptor("1", mockXssDescriptor);
    }
}
