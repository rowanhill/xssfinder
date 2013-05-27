package org.xssfinder.reflection;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class InstantiatorTest {
    @Test
    public void instantiatesNullConstructor() throws Exception {
        // given
        Instantiator instantiator = new Instantiator();

        // when
        SomeClass someClass = instantiator.instantiate(SomeClass.class);

        // then
        assertThat(someClass, is(not(nullValue())));
    }

    @Test(expected=InstantiationException.class)
    public void throwsInstantiationExceptionIfNoNullConstructor() throws Exception {
        // given
        Instantiator instantiator = new Instantiator();

        // when
        instantiator.instantiate(SomeBadClass.class);
    }

    private static class SomeClass {}

    private static class SomeBadClass {
        SomeBadClass(int dummy) {}
    }
}
