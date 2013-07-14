package org.xssfinder.runner;

import org.junit.Test;
import org.xssfinder.LabelledXssGenerator;
import org.xssfinder.reporting.XssJournal;
import org.xssfinder.routing.PageTraversal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class LabelledXssGeneratorFactoryTest {
    @Test
    public void createsLabelledXssGeneratorImplFactoryFromPageTraversal() {
        // given
        XssJournal mockJournal = mock(XssJournal.class);
        LabelledXssGeneratorFactory factory = new LabelledXssGeneratorFactory();
        PageTraversal mockTraversal = mock(PageTraversal.class);

        // when
        LabelledXssGenerator generator = factory.createLabelledXssGenerator(mockTraversal, mockJournal);

        // then
        assertThat(generator, is(notNullValue()));
    }
}
