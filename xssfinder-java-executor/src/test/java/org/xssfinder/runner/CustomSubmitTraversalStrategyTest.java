package org.xssfinder.runner;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CustomSubmitter;
import org.xssfinder.CustomTraverser;
import org.xssfinder.Page;
import org.xssfinder.remote.TraversalMode;

import java.lang.reflect.Method;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomSubmitTraversalStrategyTest {
    @Mock
    private CustomSubmitterInstantiator mockSubmitterInstantiator;
    @Mock
    private LabelledXssGeneratorFactory mockLabelledXssGeneratorFactory;
    @Mock
    private LabelledXssGeneratorImpl mockLabelledXssGenerator;
    @Mock
    private CustomTraverser mockCustomTraverser;
    @Mock
    private CustomSubmitter mockCustomSubmitter;
    private Method method;

    private CustomSubmitTraversalStrategy strategy;

    @Before
    public void setUp() throws Exception {
        method = RootPage.class.getMethod("goToSecondPage");
        when(mockLabelledXssGeneratorFactory.createLabelledXssGenerator()).thenReturn(mockLabelledXssGenerator);
        strategy = new CustomSubmitTraversalStrategy(mockSubmitterInstantiator, mockLabelledXssGeneratorFactory);
    }

    @Test
    public void cannotNormalSubmissionTraversalMode() {
        // when
        boolean canSatisfy = strategy.canSatisfyMethod(method, TraversalMode.NORMAL);

        // then
        assertThat(canSatisfy, is(false));
    }

    @Test
    public void cannotSatisfySubmitTraversalModeIfTraverserInstantiatorCannotProduceTraverser() {
        // when
        boolean canSatisfy = strategy.canSatisfyMethod(method, TraversalMode.SUBMIT);

        // then
        assertThat(canSatisfy, is(false));
    }

    @Test
    public void canSatisfySubmitTraversalModeIfTraverserInstantiatorProducesTraverser() {
        // given
        mockMethodAsHavingCustomSubmitter();

        // when
        boolean canSatisfy = strategy.canSatisfyMethod(method, TraversalMode.SUBMIT);

        // then
        assertThat(canSatisfy, is(true));
    }

    @Test
    public void traversingIsDelegatedToCustomSubmitter() throws Exception {
        //given
        RootPage page = new RootPage();
        mockMethodAsHavingCustomSubmitter();
        SecondPage mockSecondPage = mockCustomSubmittedSecondPage(page);

        // when
        TraversalResult result = strategy.traverse(page, method);

        // then
        assertThat(result.getPage(), is((Object) mockSecondPage));
    }

    @Test
    public void customSubmitterReturnsLabelToAttackIdMappingFromXssAttacksGeneratedWithLabelledXssGenerator() throws Exception {
        // given
        RootPage page = new RootPage();
        mockMethodAsHavingCustomSubmitter();
        Map<String, String> expectedLabelsToAttackIds = ImmutableMap.of("label", "attack ID");
        when(mockLabelledXssGenerator.getLabelsToAttackIds()).thenReturn(expectedLabelsToAttackIds);

        // when
        TraversalResult result = strategy.traverse(page, method);

        // then
        assertThat(result.getInputIdsToAttackIds(), is(expectedLabelsToAttackIds));
    }

    private void mockMethodAsHavingCustomSubmitter() {
        when(mockSubmitterInstantiator.instantiate(any(Method.class))).thenReturn(mockCustomSubmitter);
    }

    private SecondPage mockCustomSubmittedSecondPage(RootPage page) {
        SecondPage mockSecondPage = mock(SecondPage.class);
        when(mockCustomSubmitter.submit(page, mockLabelledXssGenerator)).thenReturn(mockSecondPage);
        return mockSecondPage;
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
    private static class SecondPage {}
}
