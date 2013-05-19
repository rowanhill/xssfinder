package org.xssfinder.reporting;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.runner.PageContext;
import org.xssfinder.xss.XssDescriptor;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XssJournalTest {
    @Mock
    private XssSightingFactory mockXssSightingFactory;

    @Test
    public void noXssDescriptorForXssIdByDefault() {
        // given
        XssJournal journal = new XssJournal(mockXssSightingFactory);

        // when
        XssDescriptor descriptor = journal.getDescriptorById("1");

        // then
        assertThat(descriptor, is(nullValue()));
    }

    @Test
    public void addedXssDescriptorCanBeRetrieved() {
        // given
        XssDescriptor mockDescriptor = mock(XssDescriptor.class);
        XssJournal journal = new XssJournal(mockXssSightingFactory);
        journal.addXssDescriptor("1", mockDescriptor);

        // when
        XssDescriptor descriptor = journal.getDescriptorById("1");

        // then
        assertThat(descriptor, is(mockDescriptor));
    }

    @Test
    public void canGetDescriptorsMarkedAsSuccessful() {
        // given
        PageContext mockPageContext = mock(PageContext.class);
        XssDescriptor mockDescriptor = mock(XssDescriptor.class);
        XssJournal journal = new XssJournal(mockXssSightingFactory);
        XssSighting mockSighting = mock(XssSighting.class);
        when(mockXssSightingFactory.createXssSighting(mockPageContext, mockDescriptor)).thenReturn(mockSighting);
        journal.addXssDescriptor("1", mockDescriptor);

        // when
        journal.markAsSuccessful(mockPageContext, ImmutableSet.of("1"));
        Set<XssSighting> successfulSightings = journal.getXssSightings();

        // then
        assertThat(successfulSightings.size(), is(1));
        XssSighting sighting = successfulSightings.iterator().next();
        assertThat(sighting, is(mockSighting));
    }

    @Test
    public void multipleSuccessfulXssSightingsResultInOnlyOneXssSighting() {
        // given
        PageContext mockPageContext = mock(PageContext.class);
        PageContext mockPageContext2 = mock(PageContext.class);
        XssDescriptor mockDescriptor = mock(XssDescriptor.class);
        XssJournal journal = new XssJournal(mockXssSightingFactory);
        XssSighting mockSighting = mock(XssSighting.class);
        when(mockXssSightingFactory.createXssSighting(mockPageContext, mockDescriptor)).thenReturn(mockSighting);
        journal.addXssDescriptor("1", mockDescriptor);

        // when
        journal.markAsSuccessful(mockPageContext, ImmutableSet.of("1"));
        journal.markAsSuccessful(mockPageContext, ImmutableSet.of("1"));
        journal.markAsSuccessful(mockPageContext2, ImmutableSet.of("1"));
        Set<XssSighting> successfulSightings = journal.getXssSightings();

        // then
        assertThat(successfulSightings.size(), is(1));
        XssSighting sighting = successfulSightings.iterator().next();
        assertThat(sighting, is(mockSighting));
    }
}
