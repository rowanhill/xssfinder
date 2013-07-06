package org.xssfinder.routing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.SubmitAction;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageTraversalTest {
    @Mock
    private PageDescriptor mockPageDescriptor;
    private PageTraversal.TraversalMode traversalMode = PageTraversal.TraversalMode.NORMAL;

    @Test
    public void methodIsAvailable() throws Exception {
        // given
        Method mockMethod = Page.class.getDeclaredMethod("goToPage");
        PageTraversal pageTraversal = new PageTraversal(mockMethod, mockPageDescriptor, traversalMode);

        // when
        Method method = pageTraversal.getMethod();

        // then
        assertThat(method, is(mockMethod));
    }

    @Test
    public void suppressCustomTraverserIsAvailable() throws Exception {
        // given
        Method mockMethod = Page.class.getDeclaredMethod("goToPage");
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
        Method mockMethod = Page.class.getDeclaredMethod("goToPage");
        PageTraversal pageTraversal = new PageTraversal(mockMethod, mockPageDescriptor, traversalMode);

        // when
        PageTraversal nextTraversal = pageTraversal.getNextTraversal();

        // then
        assertThat(nextTraversal, nullValue(PageTraversal.class));
    }

    @Test
    public void nextPageTraversalCanBeSet() throws Exception {
        // given
        Method mockMethod = Page.class.getDeclaredMethod("goToPage");
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
        Method mockMethod = Page.class.getDeclaredMethod("goToPage");
        PageTraversal traversal = new PageTraversal(mockMethod, mockPageDescriptor, traversalMode);

        // when
        PageTraversal clonedTraversal = traversal.clone();

        // then
        assertThat(clonedTraversal.getMethod(), is(mockMethod));
    }

    @Test
    public void cloneHasNoNextTraversalIfOriginalDoesNot() throws Exception {
        // given
        Method mockMethod = Page.class.getDeclaredMethod("goToPage");
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
        Method mockMethod = Page.class.getDeclaredMethod("goToPage");
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
        Method submitMethod = Page.class.getDeclaredMethod("submitToPage");
        PageTraversal traversal = new PageTraversal(submitMethod, mockPageDescriptor, traversalMode);

        // when
        boolean isSubmit = traversal.isSubmit();

        // then
        assertThat(isSubmit, is(true));
    }

    @Test
    public void unannotatedMethodsAreNotSubmitTraversals() throws Exception {
        // given
        Method submitMethod = Page.class.getDeclaredMethod("goToPage");
        PageTraversal traversal = new PageTraversal(submitMethod, mockPageDescriptor, traversalMode);

        // when
        boolean isSubmit = traversal.isSubmit();

        // then
        assertThat(isSubmit, is(false));
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class Page {
        Page goToPage() { return null; }
        @SubmitAction
        Page submitToPage() { return null; }
    }
}
