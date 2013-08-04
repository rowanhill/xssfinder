package org.xssfinder.runner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CustomSubmitter;
import org.xssfinder.LabelledXssGenerator;
import org.xssfinder.SubmitAction;
import org.xssfinder.reflection.*;
import org.xssfinder.reflection.InstantiationException;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomSubmitterInstantiatorTest {
    @Mock
    private Instantiator mockInstantiator;

    @Test
    public void delegatesInstantiationOfCustomSubmitter() throws Exception {
        // given
        CustomSubmitterInstantiator submitterInstantiator = new CustomSubmitterInstantiator(mockInstantiator);
        TestCustomSubmitter mockCustomSubmitter = mock(TestCustomSubmitter.class);
        when(mockInstantiator.instantiate(TestCustomSubmitter.class)).thenReturn(mockCustomSubmitter);
        Method method = SomePage.class.getMethod("submit");

        // when
        CustomSubmitter submitter = submitterInstantiator.instantiate(method);

        // then
        assertThat(submitter, is((CustomSubmitter)mockCustomSubmitter));
    }

    @Test
    public void returnsNullForUnannotatedMethod() throws Exception {
        // given
        CustomSubmitterInstantiator submitterInstantiator = new CustomSubmitterInstantiator(mockInstantiator);
        Method method = SomePage.class.getMethod("submitUnannotated");

        // when
        CustomSubmitter submitter = submitterInstantiator.instantiate(method);

        // then
        assertThat(submitter, is(nullValue()));
    }

    @Test
    public void returnsNullForAnnotatedMethodWithNoExplicitCustomSubmitter() throws Exception {
        // given
        CustomSubmitterInstantiator submitterInstantiator = new CustomSubmitterInstantiator(mockInstantiator);
        Method method = SomePage.class.getMethod("submitWithNoCustomSubmitter");

        // when
        CustomSubmitter submitter = submitterInstantiator.instantiate(method);

        // then
        assertThat(submitter, is(nullValue()));
        //noinspection unchecked
        verify(mockInstantiator, never()).instantiate(Matchers.any(Class.class));
    }

    @Test(expected = CustomSubmitterInstantiationException.class)
    public void throwsExceptionIfInstantiatorThrowsException() throws Exception {
        // given
        CustomSubmitterInstantiator submitterInstantiator = new CustomSubmitterInstantiator(mockInstantiator);
        when(mockInstantiator.instantiate(TestCustomSubmitter.class)).thenThrow(new InstantiationException(null));
        Method method = SomePage.class.getMethod("submit");

        // when
        submitterInstantiator.instantiate(method);
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class SomePage {
        @SubmitAction(TestCustomSubmitter.class)
        public SomePage submit() { return this; }

        public SomePage submitUnannotated() { return this; }

        @SubmitAction
        public SomePage submitWithNoCustomSubmitter() { return this; }
    }

    private static class TestCustomSubmitter implements CustomSubmitter {
        @Override
        public Object submit(Object page, LabelledXssGenerator xssGenerator) {
            return null;
        }
    }
}
