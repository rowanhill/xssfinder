package org.xssfinder.runner;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.routing.PageDescriptor;

import java.lang.reflect.Method;
import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DetectUntestedInputsPageStrategyTest {
    @Mock
    private PageContext mockContext;
    @Mock
    private PageDescriptor mockPageDescriptor;
    @Mock
    private DriverWrapper mockDriverWrapper;
    @Mock
    private XssJournal mockJournal;
    private Set<Method> submitMethods;

    @Before
    public void setUp() throws Exception {
        submitMethods = ImmutableSet.of(
                SomePage.class.getMethod("submitMethod1"),
                SomePage.class.getMethod("submitMethod2"),
                SomePage.class.getMethod("submitMethod3")
        );
        when(mockPageDescriptor.getSubmitMethods()).thenReturn(submitMethods);

        //noinspection unchecked
        when(mockPageDescriptor.getPageClass()).thenReturn((Class)SomePage.class);

        when(mockContext.getPageDescriptor()).thenReturn(mockPageDescriptor);
        when(mockContext.getDriverWrapper()).thenReturn(mockDriverWrapper);
    }

    @Test
    public void addsWarningIfPageDoesNotHaveEnoughSubmitActions() {
        // given
        when(mockDriverWrapper.getFormCount()).thenReturn(submitMethods.size() + 1);
        DetectUntestedInputsPageStrategy pageStrategy = new DetectUntestedInputsPageStrategy();

        // when
        pageStrategy.processPage(mockContext, mockJournal);

        // then
        verify(mockJournal).addPageClassWithUntestedInputs(SomePage.class);
    }

    @Test
    public void doesNotAddWarningIfPageHasAtLeastAsManySubmitActionsAsForms() {
        // given
        when(mockDriverWrapper.getFormCount()).thenReturn(submitMethods.size());
        DetectUntestedInputsPageStrategy pageStrategy = new DetectUntestedInputsPageStrategy();

        // when
        pageStrategy.processPage(mockContext, mockJournal);

        // then
        verify(mockJournal, never()).addPageClassWithUntestedInputs(SomePage.class);
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class SomePage {
        public SomePage submitMethod1() { return null; }
        public SomePage submitMethod2() { return null; }
        public SomePage submitMethod3() { return null; }
    }
}
