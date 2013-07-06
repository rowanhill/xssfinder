package org.xssfinder.runner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CustomTraverser;
import org.xssfinder.Page;
import org.xssfinder.TraverseWith;
import org.xssfinder.routing.PageDescriptor;
import org.xssfinder.routing.PageTraversal;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageTraverserTest {
    @Mock
    private CustomTraverserInstantiator mockTraverserInstantiator;
    @Mock
    private PageDescriptor mockPageDescriptor;
    @Mock
    private PageTraversal mockTraversal;

    private PageTraverser traverser;

    @Before
    public void setUp() throws Exception {
        when(mockTraversal.getMethod()).thenReturn(RootPage.class.getMethod("goToSecondPage"));

        traverser = new PageTraverser(mockTraverserInstantiator);
    }

    @Test
    public void invokesNoArgTraversalMethodAndReturnsResult() throws Exception {
        // given
        RootPage page = new RootPage();

        // when
        Object nextPage = traverser.traverse(page, mockTraversal);

        // then
        assertThat(nextPage, is(instanceOf(SecondPage.class)));
    }

    @Test(expected=UntraversableException.class)
    public void exceptionInvokingTraversalMethodGeneratesUntraversableException() throws Exception {
        // given
        when(mockTraversal.getMethod()).thenReturn(RootPage.class.getMethod("raiseException"));
        RootPage page = new RootPage();

        // when
        traverser.traverse(page, mockTraversal);
    }

    @Test(expected=UntraversableException.class)
    public void tryingToTraverseMethodWithArgsGeneratesUntraversableException() throws Exception {
        // given
        when(mockTraversal.getMethod()).thenReturn(RootPage.class.getMethod("withParameter", String.class));
        RootPage page = new RootPage();

        // when
        traverser.traverse(page, mockTraversal);
    }

    @Test
    public void customTraverserIsCreateToTraverseAnnotatedMethod() throws Exception {
        // given
        Method method = RootPage.class.getMethod("annotatedWithParameter", String.class);
        when(mockTraversal.getMethod()).thenReturn(method);
        RootPage page = new RootPage();
        CustomTraverser mockCustomTraverser = mock(CustomTraverser.class);
        when(mockTraverserInstantiator.instantiate(method)).thenReturn(mockCustomTraverser);
        SecondPage mockSecondPage = mock(SecondPage.class);
        when(mockCustomTraverser.traverse(page)).thenReturn(mockSecondPage);

        // when
        Object nextPage = traverser.traverse(page, mockTraversal);

        // then
        assertThat(nextPage, is((Object)mockSecondPage));
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
        @TraverseWith(AnnotatedWithParameterTraverser.class)
        public SecondPage annotatedWithParameter(String dummy) {
            return new SecondPage();
        }
    }

    @Page
    private static class SecondPage {
    }

    private static class AnnotatedWithParameterTraverser implements CustomTraverser {
        @Override
        public SecondPage traverse(Object page) {
            if (!(page instanceof RootPage)) {
                throw new UntraversableException(page.toString() + " was not instance of RootPage");
            }
            return ((RootPage)page).annotatedWithParameter("foobar");
        }
    }
}
