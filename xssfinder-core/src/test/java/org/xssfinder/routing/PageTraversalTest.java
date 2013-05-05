package org.xssfinder.routing;

import org.dummytest.simple.HomePage;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PageTraversalTest {
    @Test
    public void methodIsAvailable() {
        // given
        Method mockMethod = HomePage.class.getMethods()[0];
        PageTraversal pageTraversal = new PageTraversal(mockMethod);

        // when
        Method method = pageTraversal.getMethod();

        // then
        assertThat(method, is(mockMethod));
    }

    @Test
    public void nextPageTraversalDefaultsToNull() {
        // given
        Method mockMethod = HomePage.class.getMethods()[0];
        PageTraversal pageTraversal = new PageTraversal(mockMethod);

        // when
        PageTraversal nextTraversal = pageTraversal.getNextTraversal();

        // then
        assertThat(nextTraversal, nullValue(PageTraversal.class));
    }

    @Test
    public void nextPageTraversalCanBeSet() {
        // given
        Method mockMethod = HomePage.class.getMethods()[0];
        PageTraversal pageTraversal = new PageTraversal(mockMethod);
        PageTraversal mockPageTraversal = mock(PageTraversal.class);

        // when
        pageTraversal.setNextTraversal(mockPageTraversal);

        // then
        PageTraversal nextTraversal = pageTraversal.getNextTraversal();
        assertThat(nextTraversal, is(mockPageTraversal));
    }

    @Test
    public void cloningCreatesTraversalWithSameMethod() {
        // given
        Method mockMethod = HomePage.class.getMethods()[0];
        PageTraversal traversal = new PageTraversal(mockMethod);

        // when
        PageTraversal clonedTraversal = traversal.clone();

        // then
        assertThat(clonedTraversal.getMethod(), is(mockMethod));
    }

    @Test
    public void cloneHasNoNextTraversalIfOriginalDoesNot() {
        // given
        Method mockMethod = HomePage.class.getMethods()[0];
        PageTraversal traversal = new PageTraversal(mockMethod);


        // when
        PageTraversal cloneTraversal = traversal.clone();

        // then
        assertThat(cloneTraversal.getNextTraversal(), is(nullValue()));
    }

    @Test
    public void cloneHasNextTraversalIfOriginalDoes() {
        // given
        PageTraversal mockNextTraversal = mock(PageTraversal.class);
        PageTraversal mockCloneNextTraversal = mock(PageTraversal.class);
        when(mockNextTraversal.clone()).thenReturn(mockCloneNextTraversal);
        Method mockMethod = HomePage.class.getMethods()[0];
        PageTraversal traversal = new PageTraversal(mockMethod);
        traversal.setNextTraversal(mockNextTraversal);

        // when
        PageTraversal cloneTraversal = traversal.clone();

        // then
        assertThat(cloneTraversal.getNextTraversal(), is(mockCloneNextTraversal));
    }
}
