package org.xssfinder.xss;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class XssJournalTest {
    @Test
    public void noXssDescriptorForXssIdByDefault() {
        // given
        XssJournal journal = new XssJournal();

        // when
        XssDescriptor descriptor = journal.getDescriptorById("1");

        // then
        assertThat(descriptor, is(nullValue()));
    }

    @Test
    public void addedXssDescriptorCanBeRetrieved() {
        // given
        XssDescriptor mockDescriptor = mock(XssDescriptor.class);
        XssJournal journal = new XssJournal();
        journal.addXssDescriptor("1", mockDescriptor);

        // when
        XssDescriptor descriptor = journal.getDescriptorById("1");

        // then
        assertThat(descriptor, is(mockDescriptor));
    }
}
