package org.xssfinder.runner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CustomTraverser;
import org.xssfinder.TraverseWith;
import org.xssfinder.reflection.*;
import org.xssfinder.reflection.InstantiationException;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomTraverserInstantiatorTest {
    @Mock
    private Instantiator mockInstantiator;

    @Test
    public void delegatesInstantiationOfCustomTraverser() throws Exception {
        // given
        CustomTraverserInstantiator instantiator = new CustomTraverserInstantiator(mockInstantiator);
        Method method = SomePage.class.getMethod("submitValue", String.class);
        SomeCustomTraverser mockCustomTraverser = mock(SomeCustomTraverser.class);
        when(mockInstantiator.instantiate(SomeCustomTraverser.class)).thenReturn(mockCustomTraverser);

        // when
        CustomTraverser traverser = instantiator.instantiate(method);

        //then
        assertThat(traverser, is((CustomTraverser)mockCustomTraverser));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void returnsNullForUnannotatedMethod() throws Exception {
        // given
        CustomTraverserInstantiator instantiator = new CustomTraverserInstantiator(mockInstantiator);
        Method method = SomePage.class.getMethod("submitOtherValue", String.class);

        // when
        CustomTraverser traverser = instantiator.instantiate(method);

        //then
        assertThat(traverser, is(nullValue()));
        verify(mockInstantiator, never()).instantiate(argThat(any(Class.class)));
    }

    @Test(expected=CustomTraverserInstantiationException.class)
    public void throwsExceptionIfInstantiatorThrows() throws Exception {
        // given
        CustomTraverserInstantiator instantiator = new CustomTraverserInstantiator(mockInstantiator);
        Method method = SomePage.class.getMethod("submitWithBadConstructor", String.class);
        when(mockInstantiator.instantiate(BadConstructorTraverser.class)).thenThrow(new InstantiationException(null));

        // when
        instantiator.instantiate(method);
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    private static class SomePage {
        @TraverseWith(SomeCustomTraverser.class)
        public SomePage submitValue(String value) {
            return this;
        }
        public SomePage submitOtherValue(String value) {
            return this;
        }
        @TraverseWith(BadConstructorTraverser.class)
        public SomePage submitWithBadConstructor(String value) {
            return this;
        }
    }

    private static class SomeCustomTraverser implements CustomTraverser {
        @Override
        public SomePage traverse(Object page) {
            if (!(page instanceof SomePage)) {
                throw new UntraversableException(page.toString() + " was not instance of SomePage");
            }
            return ((SomePage)page).submitValue("some value");
        }
    }

    @SuppressWarnings("UnusedParameters")
    private static class BadConstructorTraverser implements CustomTraverser {
        BadConstructorTraverser(int dummy) {}
        @Override
        public Object traverse(Object page) {
            return null;
        }
    }
}
