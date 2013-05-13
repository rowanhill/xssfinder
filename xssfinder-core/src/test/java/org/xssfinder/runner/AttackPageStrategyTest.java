package org.xssfinder.runner;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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

    @Mock
    private PageContext mockContext;

    @Test
    public void processingPageAddsAttackDescriptorsToJournal() {
        // given
        XssDescriptor mockXssDescriptor = mock(XssDescriptor.class);
        Map<String, XssDescriptor> descriptorsById = ImmutableMap.of(
                "1", mockXssDescriptor
        );
        when(mockPageAttacker.attackIfAboutToSubmit(mockContext))
                .thenReturn(descriptorsById);

        AttackPageStrategy strategy = new AttackPageStrategy(mockPageAttacker);

        // when
        strategy.processPage(mockContext, mockXssJournal);

        // then
        verify(mockXssJournal).addXssDescriptor("1", mockXssDescriptor);
    }
}
