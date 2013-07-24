package org.xssfinder.runner;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.routing.PageDescriptor;

import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DetectUntestedInputsPageStrategyTest {
    @Mock
    private PageContext mockContext;
    @Mock
    private PageDescriptor mockPageDescriptor;
    @Mock
    private ExecutorWrapper mockExecutor;
    @Mock
    private XssJournal mockJournal;
    @Mock
    private PageDefinition mockPageDefinition;

    private Set<MethodDefinition> submitMethods;

    @Before
    public void setUp() throws Exception {
        MethodDefinition mockMethodDefinition1 = mock(MethodDefinition.class);
        MethodDefinition mockMethodDefinition2 = mock(MethodDefinition.class);
        MethodDefinition mockMethodDefinition3 = mock(MethodDefinition.class);
        submitMethods = ImmutableSet.of(
                mockMethodDefinition1,
                mockMethodDefinition2,
                mockMethodDefinition3
        );
        when(mockPageDescriptor.getSubmitMethods()).thenReturn(submitMethods);

        when(mockPageDescriptor.getPageDefinition()).thenReturn(mockPageDefinition);

        when(mockContext.getPageDescriptor()).thenReturn(mockPageDescriptor);
        when(mockContext.getExecutor()).thenReturn(mockExecutor);
    }

    @Test
    public void addsWarningIfPageDoesNotHaveEnoughSubmitActions() throws Exception {
        // given
        when(mockExecutor.getFormCount()).thenReturn(submitMethods.size() + 1);
        DetectUntestedInputsPageStrategy pageStrategy = new DetectUntestedInputsPageStrategy();

        // when
        pageStrategy.processPage(mockContext, mockJournal);

        // then
        verify(mockJournal).addPageClassWithUntestedInputs(mockPageDefinition);
    }

    @Test
    public void doesNotAddWarningIfPageHasAtLeastAsManySubmitActionsAsForms() throws Exception {
        // given
        when(mockExecutor.getFormCount()).thenReturn(submitMethods.size());
        DetectUntestedInputsPageStrategy pageStrategy = new DetectUntestedInputsPageStrategy();

        // when
        pageStrategy.processPage(mockContext, mockJournal);

        // then
        verify(mockJournal, never()).addPageClassWithUntestedInputs(mockPageDefinition);
    }
}
