package org.xssfinder.reporting;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.runner.PageContext;
import org.xssfinder.xss.XssDescriptor;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.empty;
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

    @Test
    public void canGetUntestedInputWarnings() {
        // given
        XssJournal journal = new XssJournal(mockXssSightingFactory);

        // when
        journal.addPageClassWithUntestedInputs(SomePage.class);
        journal.addPageClassWithUntestedInputs(OtherPage.class);
        Set<Class<?>> pagesWithUntestedInputs = journal.getPagesClassWithUntestedInputs();

        // then
        Set<Class<?>> expectedPageClasses = ImmutableSet.of(SomePage.class, OtherPage.class);
        assertThat(pagesWithUntestedInputs, is(expectedPageClasses));
    }

    @Test
    public void addingWarningMultipleTimesResultsInJustOneWarning() {
        // given
        XssJournal journal = new XssJournal(mockXssSightingFactory);

        // when
        journal.addPageClassWithUntestedInputs(SomePage.class);
        journal.addPageClassWithUntestedInputs(SomePage.class);
        Set<Class<?>> pagesWithUntestedInputs = journal.getPagesClassWithUntestedInputs();

        // then
        assertThat(pagesWithUntestedInputs.size(), is(1));
        assertThat(pagesWithUntestedInputs.iterator().next() == SomePage.class, is(true));
    }

    @Test
    public void errorContextsStartsEmpty() {
        // given
        XssJournal journal = new XssJournal(mockXssSightingFactory);

        // when
        List<RouteRunErrorContext> errorContexts = journal.getErrorContexts();

        // then
        assertThat(errorContexts, is(empty()));
    }

    @Test
    public void addedErrorContextIsLogged() {
        // given
        XssJournal journal = new XssJournal(mockXssSightingFactory);
        RouteRunErrorContext mockErrorContext = mock(RouteRunErrorContext.class);

        // when
        journal.addErrorContext(mockErrorContext);
        List<RouteRunErrorContext> errorContexts = journal.getErrorContexts();

        // then
        List<RouteRunErrorContext> expectedContexts = ImmutableList.of(
                mockErrorContext
        );
        assertThat(errorContexts, is(expectedContexts));
    }

    private static class SomePage {}
    private static class OtherPage {}
}
