package org.xssfinder.runner;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class LabelledXssGeneratorFactoryTest {
    @Test
    public void createsLabelledXssGeneratorImplFactoryFromPageTraversal() {
        // given
        LabelledXssGeneratorFactory factory = new LabelledXssGeneratorFactory();

        // when
        LabelledXssGeneratorImpl generator = factory.createLabelledXssGenerator();

        // then
        assertThat(generator, is(notNullValue()));
    }
}
