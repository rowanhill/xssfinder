package org.xssfinder.runner;

import org.junit.Test;
import org.xssfinder.Page;
import org.xssfinder.routing.PageTraversal;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PageTraverserTest {

    @Test
    public void invokesNoArgTraversalMethodAndReturnsResult() throws Exception {
        // given
        PageTraverser traverser = new PageTraverser();
        PageTraversal traversal = new PageTraversal(RootPage.class.getMethod("goToSecondPage"));
        RootPage page = new RootPage();

        // when
        Object nextPage = traverser.traverse(page, traversal);

        // then
        assertThat(nextPage, is(instanceOf(SecondPage.class)));
    }

    @Test(expected=UntraversableException.class)
    public void exceptionInvokingTraversalMethodGeneratesUntraversableException() throws Exception {
        // given
        PageTraverser traverser = new PageTraverser();
        PageTraversal traversal = new PageTraversal(RootPage.class.getMethod("raiseException"));
        RootPage page = new RootPage();

        // when
        traverser.traverse(page, traversal);
    }

    @Test(expected=UntraversableException.class)
    public void tryingToTraverseMethodWithArgsGeneratesUntraversableException() throws Exception {
        // given
        PageTraverser traverser = new PageTraverser();
        PageTraversal traversal = new PageTraversal(RootPage.class.getMethod("withParameter", String.class));
        RootPage page = new RootPage();

        // when
        traverser.traverse(page, traversal);
    }

    @SuppressWarnings("UnusedDeclaration")
    @Page
    private static class RootPage {
        public SecondPage goToSecondPage() {
            return new SecondPage();
        }
        public SecondPage raiseException() {
            throw new RuntimeException();
        }
        public SecondPage withParameter(String dummy) {
            return new SecondPage();
        }
    }

    @Page
    private static class SecondPage {
    }
}
