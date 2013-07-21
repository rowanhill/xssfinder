package org.xssfinder.runner;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.ExecutorWrapper;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;
import org.xssfinder.remote.TraversalMode;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.routing.PageDescriptor;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.xss.XssDescriptor;
import org.xssfinder.xss.XssDescriptorFactory;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PageContextTest {
    @Mock
    private ExecutorWrapper mockExecutor;
    @Mock
    private XssJournal mockXssJournal;
    @Mock
    private XssDescriptorFactory mockXssDescriptorFactory;
    @Mock
    private PageTraversal mockPageTraversal;
    @Mock
    private PageDescriptor mockPageDescriptor;

    @Before
    public void setUp() {
        when(mockPageTraversal.getTraversalMode()).thenReturn(PageTraversal.TraversalMode.NORMAL);
    }

    @Test
    public void pageIsAvailable() {
        // given
        PageDefinition mockPageDefinition = mock(PageDefinition.class);
        when(mockPageDescriptor.getPageDefinition()).thenReturn(mockPageDefinition);
        PageContext context = new PageContext(mockExecutor, mockXssJournal, mockXssDescriptorFactory, mockPageTraversal, mockPageDescriptor);

        // when
        PageDefinition pageDefinition = context.getPageDefinition();

        // then
        assertThat(pageDefinition, is(mockPageDefinition));
    }

    @Test
    public void driverWrapperIsAvailable() {
        // given
        PageContext context = new PageContext(mockExecutor, mockXssJournal, mockXssDescriptorFactory, mockPageTraversal, mockPageDescriptor);

        // when
        ExecutorWrapper executor = context.getExecutor();

        // then
        assertThat(executor, is(mockExecutor));
    }

    @Test
    public void traversalIsAvailable() {
        // given
        PageContext context = new PageContext(mockExecutor, mockXssJournal, mockXssDescriptorFactory, mockPageTraversal, mockPageDescriptor);

        // when
        PageTraversal traversal = context.getPageTraversal();

        // then
        assertThat(traversal, is(mockPageTraversal));
    }

    @Test
    public void pageDescriptorIsAvailable() {
        // given
        PageContext context = new PageContext(mockExecutor, mockXssJournal, mockXssDescriptorFactory, mockPageTraversal, mockPageDescriptor);

        // when
        PageDescriptor descriptor = context.getPageDescriptor();

        // then
        assertThat(descriptor, is(mockPageDescriptor));
    }

    @Test
    public void hasNextContextIsTrueIfPageTraversalIsNotNull() {
        // given
        PageContext context = new PageContext(mockExecutor, mockXssJournal, mockXssDescriptorFactory, mockPageTraversal, mockPageDescriptor);

        // when
        boolean hasNextContext = context.hasNextContext();

        // then
        assertThat(hasNextContext, is(true));
    }

    @Test
    public void hasNextContextIsFalseIfPageTraversalIsNull() {
        // given
        PageContext context = new PageContext(mockExecutor, mockXssJournal, mockXssDescriptorFactory, null, mockPageDescriptor);

        // when
        boolean hasNextContext = context.hasNextContext();

        // then
        assertThat(hasNextContext, is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void getNextContextThrowsExceptionIfPageTraversalIsNull() throws Exception {
        // given
        PageContext context = new PageContext(mockExecutor, mockXssJournal, mockXssDescriptorFactory, null, mockPageDescriptor);

        // when
        context.getNextContext();
    }

    @Test
    public void nextContextPageIsGeneratedByPageTraverser() throws Exception {
        // given
        PageDefinition mockNextPageDefinition = mockResultingPageDefinition(mockPageTraversal);
        PageContext context = new PageContext(mockExecutor, mockXssJournal, mockXssDescriptorFactory, mockPageTraversal, mockPageDescriptor);

        // when
        PageContext nextContext = context.getNextContext();
        PageDefinition nextPage = nextContext.getPageDefinition();

        // then
        assertThat(nextPage, is(mockNextPageDefinition));
    }

    @Test
    public void nextButOnePageIsGeneratedFromSecondTraversal() throws Exception {
        // given
        PageTraversal mockNextTraversal = mock(PageTraversal.class);
        PageDefinition mockNextButOnePageDefinition = mockResultingPageDefinition(mockNextTraversal);
        when(mockPageTraversal.getNextTraversal()).thenReturn(mockNextTraversal);
        when(mockNextTraversal.getTraversalMode()).thenReturn(PageTraversal.TraversalMode.NORMAL);
        PageContext context = new PageContext(mockExecutor, mockXssJournal, mockXssDescriptorFactory, mockPageTraversal, mockPageDescriptor);

        // when
        PageContext nextContext = context.getNextContext();
        PageContext nextButOneContext = nextContext.getNextContext();
        PageDefinition nextButOnePageDefinition = nextButOneContext.getPageDefinition();

        // then
        assertThat(nextButOnePageDefinition, is(mockNextButOnePageDefinition));
    }

    @Test
    public void xssAttacksCreatedByTraversingAreAddedToXssJournal() throws Exception {
        // given
        PageContext context = new PageContext(mockExecutor, mockXssJournal, mockXssDescriptorFactory, mockPageTraversal, mockPageDescriptor);
        MethodDefinition mockMethodDefinition = mock(MethodDefinition.class);
        when(mockPageTraversal.getMethod()).thenReturn(mockMethodDefinition);
        when(mockExecutor.traverseMethod(mockMethodDefinition, TraversalMode.NORMAL)).thenReturn(ImmutableMap.of(
                "inputId", "attackId"
        ));
        XssDescriptor mockXssDescriptor = mock(XssDescriptor.class);
        when(mockXssDescriptorFactory.createXssDescriptor(mockPageTraversal, "inputId")).thenReturn(mockXssDescriptor);

        // when
        context.getNextContext();

        // then
        verify(mockXssJournal).addXssDescriptor("attackId", mockXssDescriptor);
    }

    private PageDefinition mockResultingPageDefinition(PageTraversal mockPageTraversal) {
        PageDefinition mockNextPageDefinition = mock(PageDefinition.class);
        PageDescriptor mockNextPageDescriptor = mock(PageDescriptor.class);
        when(mockNextPageDescriptor.getPageDefinition()).thenReturn(mockNextPageDefinition);
        when(mockPageTraversal.getResultingPageDescriptor()).thenReturn(mockNextPageDescriptor);
        return mockNextPageDefinition;
    }
}
