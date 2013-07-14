package org.xssfinder.routing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageTraversalTest {
    @Mock
    private PageDescriptor mockPageDescriptor;
    @Mock
    private MethodDefinition mockMethod;
    private PageTraversal.TraversalMode traversalMode = PageTraversal.TraversalMode.NORMAL;

    @Before
    public void setUp() {
        when(mockMethod.getIdentifier()).thenReturn("goToPage");
        PageDefinition mockPageDefinition = mock(PageDefinition.class);
        when(mockPageDefinition.getIdentifier()).thenReturn("Page");
        when(mockMethod.getReturnType()).thenReturn(mockPageDefinition);
    }

    @Test
    public void methodIsAvailable() throws Exception {
        // given
        PageTraversal pageTraversal = new PageTraversal(mockMethod, mockPageDescriptor, traversalMode);

        // when
        MethodDefinition method = pageTraversal.getMethod();

        // then
        assertThat(method, is(mockMethod));
    }

    @Test
    public void suppressCustomTraverserIsAvailable() throws Exception {
        // given
        PageTraversal nonSuppressingPageTraversal = new PageTraversal(mockMethod, mockPageDescriptor, PageTraversal.TraversalMode.NORMAL);
        PageTraversal suppressingPageTraversal = new PageTraversal(mockMethod, mockPageDescriptor, PageTraversal.TraversalMode.SUBMIT);

        // when
        PageTraversal.TraversalMode suppressingTraversalsMode = suppressingPageTraversal.getTraversalMode();
        PageTraversal.TraversalMode nonSuppressingTraversalsMode = nonSuppressingPageTraversal.getTraversalMode();

        // then
        assertThat(suppressingTraversalsMode, is(PageTraversal.TraversalMode.SUBMIT));
        assertThat(nonSuppressingTraversalsMode, is(PageTraversal.TraversalMode.NORMAL));
    }

    @Test
    public void nextPageTraversalDefaultsToNull() throws Exception {
        // given
        PageTraversal pageTraversal = new PageTraversal(mockMethod, mockPageDescriptor, traversalMode);

        // when
        PageTraversal nextTraversal = pageTraversal.getNextTraversal();

        // then
        assertThat(nextTraversal, nullValue(PageTraversal.class));
    }

    @Test
    public void nextPageTraversalCanBeSet() throws Exception {
        // given
        PageTraversal pageTraversal = new PageTraversal(mockMethod, mockPageDescriptor, traversalMode);
        PageTraversal mockPageTraversal = mock(PageTraversal.class);

        // when
        pageTraversal.setNextTraversal(mockPageTraversal);

        // then
        PageTraversal nextTraversal = pageTraversal.getNextTraversal();
        assertThat(nextTraversal, is(mockPageTraversal));
    }

    @Test
    public void cloningCreatesTraversalWithSameMethod() throws Exception {
        // given
        PageTraversal traversal = new PageTraversal(mockMethod, mockPageDescriptor, traversalMode);

        // when
        PageTraversal clonedTraversal = traversal.clone();

        // then
        assertThat(clonedTraversal.getMethod(), is(mockMethod));
    }

    @Test
    public void cloneHasNoNextTraversalIfOriginalDoesNot() throws Exception {
        // given
        PageTraversal traversal = new PageTraversal(mockMethod, mockPageDescriptor, traversalMode);

        // when
        PageTraversal cloneTraversal = traversal.clone();

        // then
        assertThat(cloneTraversal.getNextTraversal(), is(nullValue()));
    }

    @Test
    public void cloneHasNextTraversalIfOriginalDoes() throws Exception {
        // given
        PageTraversal mockNextTraversal = mock(PageTraversal.class);
        PageTraversal mockCloneNextTraversal = mock(PageTraversal.class);
        when(mockNextTraversal.clone()).thenReturn(mockCloneNextTraversal);
        PageTraversal traversal = new PageTraversal(mockMethod, mockPageDescriptor, traversalMode);
        traversal.setNextTraversal(mockNextTraversal);

        // when
        PageTraversal cloneTraversal = traversal.clone();

        // then
        assertThat(cloneTraversal.getNextTraversal(), is(mockCloneNextTraversal));
    }

    @Test
    public void annotatedMethodsAreSubmitTraversals() throws Exception {
        // given
        when(mockMethod.isSubmitAnnotated()).thenReturn(true);
        PageTraversal traversal = new PageTraversal(mockMethod, mockPageDescriptor, traversalMode);

        // when
        boolean isSubmit = traversal.isSubmit();

        // then
        assertThat(isSubmit, is(true));
    }

    @Test
    public void unannotatedMethodsAreNotSubmitTraversals() throws Exception {
        // given
        when(mockMethod.isSubmitAnnotated()).thenReturn(false);
        PageTraversal traversal = new PageTraversal(mockMethod, mockPageDescriptor, traversalMode);

        // when
        boolean isSubmit = traversal.isSubmit();

        // then
        assertThat(isSubmit, is(false));
    }

    @Test
    public void includesMethodAndTraversalModeInToString() throws Exception {
        // given
        when(mockMethod.isSubmitAnnotated()).thenReturn(true);
        PageTraversal traversal = new PageTraversal(mockMethod, mockPageDescriptor, traversalMode);

        // when
        String toString = traversal.toString();

        // then
        assertThat(toString, is("{goToPage, Normal} -> Page"));
    }

    @Test
    public void includesNextTraversalInToString() throws Exception {
        // given
        when(mockMethod.isSubmitAnnotated()).thenReturn(true);
        PageTraversal traversal = new PageTraversal(mockMethod, mockPageDescriptor, traversalMode);
        PageTraversal mockNextTraversal = mock(PageTraversal.class);
        when(mockNextTraversal.toString()).thenReturn("<child traversal>");
        traversal.setNextTraversal(mockNextTraversal);

        // when
        String toString = traversal.toString();

        // then
        assertThat(toString, is("{goToPage, Normal} -> Page -> <child traversal>"));
    }
}
