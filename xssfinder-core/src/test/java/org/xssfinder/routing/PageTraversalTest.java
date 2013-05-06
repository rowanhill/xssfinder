package org.xssfinder.routing;

import org.junit.Test;
import org.xssfinder.SubmitAction;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PageTraversalTest {
    @Test
    public void methodIsAvailable() throws Exception {
        // given
        Method mockMethod = Page.class.getDeclaredMethod("goToPage");
        PageTraversal pageTraversal = new PageTraversal(mockMethod);

        // when
        Method method = pageTraversal.getMethod();

        // then
        assertThat(method, is(mockMethod));
    }

    @Test
    public void nextPageTraversalDefaultsToNull() throws Exception {
        // given
        Method mockMethod = Page.class.getDeclaredMethod("goToPage");
        PageTraversal pageTraversal = new PageTraversal(mockMethod);

        // when
        PageTraversal nextTraversal = pageTraversal.getNextTraversal();

        // then
        assertThat(nextTraversal, nullValue(PageTraversal.class));
    }

    @Test
    public void nextPageTraversalCanBeSet() throws Exception {
        // given
        Method mockMethod = Page.class.getDeclaredMethod("goToPage");
        PageTraversal pageTraversal = new PageTraversal(mockMethod);
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
        PageTraversal traversal = new PageTraversal(mockMethod);

        // when
        PageTraversal clonedTraversal = traversal.clone();

        // then
        assertThat(clonedTraversal.getMethod(), is(mockMethod));
    }

    @Test
    public void cloneHasNoNextTraversalIfOriginalDoesNot() throws Exception {
        // given
        Method mockMethod = Page.class.getDeclaredMethod("goToPage");
        PageTraversal traversal = new PageTraversal(mockMethod);

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
        PageTraversal traversal = new PageTraversal(mockMethod);
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
        PageTraversal traversal = new PageTraversal(submitMethod);

        // when
        boolean isSubmit = traversal.isSubmit();

        // then
        assertThat(isSubmit, is(true));
    }

    @Test
    public void unannotatedMethodsAreNotSubmitTraversals() throws Exception {
        // given
        Method submitMethod = Page.class.getDeclaredMethod("goToPage");
        PageTraversal traversal = new PageTraversal(submitMethod);

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
