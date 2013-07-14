package org.xssfinder.routing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.remote.MethodDefinition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UntraversedSubmitMethodsFinderTest {
    @Mock
    private PageDescriptor mockPageDescriptor;
    @Mock
    private Route mockRoute;
    @Mock
    private MethodDefinition mockMethodDefinition;

    private final List<Route> routes = new ArrayList<Route>();
    private final Set<PageDescriptor> descriptors = new HashSet<PageDescriptor>();

    private final UntraversedSubmitMethodsFinder finder = new UntraversedSubmitMethodsFinder();

    @Before
    public void setUp() {
        descriptors.add(mockPageDescriptor);
        routes.add(mockRoute);
    }

    @Test
    public void findsNoMethodsIfPageDescriptorsHaveNoSubmitMethods() {
        // when
        SetMultimap<PageDescriptor, MethodDefinition> untraversedSubmits = finder.getUntraversedSubmitMethods(routes, descriptors);

        // then
        assertThat(untraversedSubmits.isEmpty(), is(true));
    }

    @Test
    public void findsNoMethodsIfPageDescriptorSubmitsAreUsedInRoute() throws Exception {
        // given

        when(mockPageDescriptor.getSubmitMethods()).thenReturn(ImmutableSet.of(
                mockMethodDefinition
        ));
        when(mockRoute.getTraversedSubmitMethods()).thenReturn(ImmutableSet.of(
                mockMethodDefinition
        ));

        // when
        SetMultimap<PageDescriptor, MethodDefinition> untraversedSubmits = finder.getUntraversedSubmitMethods(routes, descriptors);

        // then
        assertThat(untraversedSubmits.isEmpty(), is(true));
    }

    @Test
    public void findsMethodsIfNotInRoute() throws Exception {
        // given
        when(mockPageDescriptor.getSubmitMethods()).thenReturn(ImmutableSet.of(
                mockMethodDefinition
        ));

        // when
        SetMultimap<PageDescriptor, MethodDefinition> untraversedSubmits = finder.getUntraversedSubmitMethods(routes, descriptors);

        // then
        assertThat(untraversedSubmits.size(), is(1));
        Set<MethodDefinition> expectedMethods = ImmutableSet.of(
                mockMethodDefinition
        );
        assertThat(untraversedSubmits.get(mockPageDescriptor), is(expectedMethods));
    }
}
