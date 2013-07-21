package org.xssfinder.runner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.routing.PageDescriptor;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.routing.Route;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageContextFactoryTest {
    @Mock
    private Route mockRoute;
    @Mock
    private XssJournal mockXssJournal;
    @Mock
    private ExecutorWrapper mockExecutor;
    @Mock
    private PageTraversal mockPageTraversal;

    @SuppressWarnings("unchecked")
    @Test
    public void constructsPageContexts() {
        // given
        PageContextFactory factory = new PageContextFactory(mockExecutor, mockXssJournal);
        PageTraversal mockPageTraversal = mock(PageTraversal.class);
        PageDescriptor mockPageDescriptor = mock(PageDescriptor.class);
        when(mockRoute.getPageTraversal()).thenReturn(mockPageTraversal);
        when(mockRoute.getRootPageDescriptor()).thenReturn(mockPageDescriptor);

        // when
        PageContext context = factory.createContext(mockRoute);

        // then
        assertThat(context, is(notNullValue()));
        assertThat(context.getPageTraversal(), is(mockPageTraversal));
        assertThat(context.getPageDescriptor(), is(mockPageDescriptor));
        assertThat(context.getExecutor(), is(mockExecutor));
    }
}
