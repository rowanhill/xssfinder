package org.xssfinder.routing;

import org.dummytest.simple.HomePage;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;

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
}
