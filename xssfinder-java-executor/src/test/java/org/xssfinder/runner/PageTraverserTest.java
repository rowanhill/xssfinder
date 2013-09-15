package org.xssfinder.runner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.Page;
import org.xssfinder.remote.TUntraversableException;
import org.xssfinder.remote.TWebInteractionException;
import org.xssfinder.remote.TraversalMode;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageTraverserTest {
    @Mock
    private CustomNormalTraversalStrategy mockNormalStrategy;
    @Mock
    private CustomSubmitTraversalStrategy mockSubmitStrategy;
    @Mock
    private SimpleMethodTraversalStrategy mockMethodStrategy;
    private Method method;
    private RootPage rootPage;
    private TraversalMode traversalMode = TraversalMode.NORMAL;

    private PageTraverser traverser;

    @Before
    public void setUp() throws Exception {
        rootPage = new RootPage();
        method = RootPage.class.getMethod("goToSecondPage");
        traverser = new PageTraverser(mockNormalStrategy, mockSubmitStrategy, mockMethodStrategy);
    }

    @Test
    public void traversalSatisfiedByNormalStrategyIsDelegatedToNormalStrategy() throws Exception {
        // given
        TraversalResult mockResult = mockCanSatisfyAndTraverseForStrategy(mockNormalStrategy, traversalMode);

        // when
        TraversalResult result = traverser.traverse(rootPage, method, traversalMode);

        // then
        assertThat(result, is(mockResult));
    }

    @Test
    public void traversalSatisfiedBySubmitStrategyIsDelegatedToSubmitStrategy() throws Exception {
        // given
        TraversalResult mockResult = mockCanSatisfyAndTraverseForStrategy(mockSubmitStrategy, traversalMode);

        // when
        TraversalResult result = traverser.traverse(rootPage, method, traversalMode);

        // then
        assertThat(result, is(mockResult));
    }

    @Test
    public void traversalSatisfiedBySimpleMethodStrategyIsDelegatedToSimpleMethodStrategy() throws Exception {
        // given
        TraversalResult mockResult = mockCanSatisfyAndTraverseForStrategy(mockMethodStrategy, traversalMode);

        // when
        TraversalResult result = traverser.traverse(rootPage, method, traversalMode);

        // then
        assertThat(result, is(mockResult));
    }

    @Test
    public void normalStrategyTakesPrecedentOverSubmitAndSimpleMethodStrategies() throws Exception {
        // given
        TraversalResult mockNormalResult = mockCanSatisfyAndTraverseForStrategy(mockNormalStrategy, traversalMode);
        mockCanSatisfyAndTraverseForStrategy(mockSubmitStrategy, traversalMode);
        mockCanSatisfyAndTraverseForStrategy(mockMethodStrategy, traversalMode);

        // when
        TraversalResult result = traverser.traverse(rootPage, method, traversalMode);

        // then
        assertThat(result, is(mockNormalResult));
    }

    @Test
    public void submitStrategyTakesPrecedentOverSimpleMethodStrategy() throws Exception {
        // given
        TraversalResult mockSubmitResult = mockCanSatisfyAndTraverseForStrategy(mockSubmitStrategy, traversalMode);
        mockCanSatisfyAndTraverseForStrategy(mockMethodStrategy, traversalMode);

        // when
        TraversalResult result = traverser.traverse(rootPage, method, traversalMode);

        // then
        assertThat(result, is(mockSubmitResult));
    }

    @Test(expected=TUntraversableException.class)
    public void thrownTUntraversableExceptionIsRethrown() throws Exception {
        // given
        mockCanSatisfyAndTraverseForStrategy(mockMethodStrategy, traversalMode);
        TUntraversableException untraversableException = new TUntraversableException();
        when(mockMethodStrategy.traverse(rootPage, method)).thenThrow(untraversableException);

        // when
        traverser.traverse(rootPage, method, traversalMode);
    }

    @Test(expected=TWebInteractionException.class)
    public void thrownExceptionCausesTWebInteractionExceptionToBeThrown() throws Exception {
        // given
        mockCanSatisfyAndTraverseForStrategy(mockMethodStrategy, traversalMode);
        Exception exception = new RuntimeException();
        when(mockMethodStrategy.traverse(rootPage, method)).thenThrow(exception);

        // when
        traverser.traverse(rootPage, method, traversalMode);
    }

    private TraversalResult mockCanSatisfyAndTraverseForStrategy(
            TraversalStrategy mockStrategy, TraversalMode traversalMode
    ) throws Exception {
        TraversalResult mockResult = mock(TraversalResult.class);
        when(mockStrategy.canSatisfyMethod(method, traversalMode)).thenReturn(true);
        when(mockStrategy.traverse(rootPage, method)).thenReturn(mockResult);
        return mockResult;
    }

    @SuppressWarnings("UnusedDeclaration")
    @Page
    private static class RootPage {
        public SecondPage goToSecondPage() {
            return new SecondPage();
        }
    }

    @Page
    private static class SecondPage {}
}
