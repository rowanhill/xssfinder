package org.xssfinder.routing;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PageTraversalFactoryTest {
    @Test
    public void predecessorTraversalHasPredecessorTraversalMethodAndResultingPageDescriptor() throws Exception {
        // given
        PageTraversalFactory factory = new PageTraversalFactory();
        GraphNode mockNode = mock(GraphNode.class);
        Method method = SomePage.class.getMethod("someLink");
        when(mockNode.getPredecessorTraversalMethod()).thenReturn(method);
        PageDescriptor mockPageDescriptor = mock(PageDescriptor.class);
        when(mockNode.getPageDescriptor()).thenReturn(mockPageDescriptor);

        // when
        PageTraversal traversal = factory.createTraversalToNode(mockNode, PageTraversal.TraversalMode.NORMAL);

        // then
        assertThat(traversal.getMethod(), is(method));
        assertThat(traversal.getResultingPageDescriptor(), is(mockPageDescriptor));
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class SomePage {
        public SomePage someLink() { return null; }
    }
}
