package org.xssfinder.runner;

import org.junit.Test;
import org.xssfinder.CustomTraverser;
import org.xssfinder.TraverseWith;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class CustomTraverserInstantiatorTest {
    @Test
    public void createsCustomTraverserFromNullConstructor() throws Exception {
        // given
        CustomTraverserInstantiator instantiator = new CustomTraverserInstantiator();
        Method method = SomePage.class.getMethod("submitValue", String.class);

        // when
        CustomTraverser traverser = instantiator.instantiate(method);

        //then
        assertThat(traverser, is(instanceOf(SomeCustomTraverser.class)));
    }

    @Test
    public void returnsNullForUnannotatedMethod() throws Exception {
        // given
        CustomTraverserInstantiator instantiator = new CustomTraverserInstantiator();
        Method method = SomePage.class.getMethod("submitOtherValue", String.class);

        // when
        CustomTraverser traverser = instantiator.instantiate(method);

        //then
        assertThat(traverser, is(nullValue()));
    }

    @Test(expected=CustomTraverserInstantiationException.class)
    public void throwsExceptionIfNullConstructorDoesNotExist() throws Exception {
        // given
        CustomTraverserInstantiator instantiator = new CustomTraverserInstantiator();
        Method method = SomePage.class.getMethod("submitWithBadConstructor", String.class);

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

    private static class BadConstructorTraverser implements CustomTraverser {
        BadConstructorTraverser(int dummy) {}
        @Override
        public Object traverse(Object page) {
            return null;
        }
    }
}
