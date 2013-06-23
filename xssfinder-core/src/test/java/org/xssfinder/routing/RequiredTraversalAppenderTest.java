package org.xssfinder.routing;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequiredTraversalAppenderTest {
    @Mock
    private UntraversedSubmitMethodsFinder mockUntraversedSubmitMethodFinder;
    @Mock
    private Route mockRoute;
    @Mock
    private PageDescriptor mockPageDescriptor;
    @Mock
    private PageDescriptor mockOtherPageDescriptor;
    @Mock
    private PageDescriptor mockAnotherPageDescriptor;

    private List<Route> routes;
    private Set<PageDescriptor> pageDescriptors;
    private SetMultimap<PageDescriptor, Method> descriptorsToMethods;

    @InjectMocks
    private RequiredTraversalAppender traversalAppender;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        routes = new ArrayList<Route>();
        routes.add(mockRoute);

        pageDescriptors = new HashSet<PageDescriptor>();
        pageDescriptors.add(mockPageDescriptor);
        pageDescriptors.add(mockOtherPageDescriptor);
        pageDescriptors.add(mockAnotherPageDescriptor);

        when(mockPageDescriptor.getPageClass()).thenReturn((Class)SomePage.class);
        when(mockOtherPageDescriptor.getPageClass()).thenReturn((Class)SomeOtherPage.class);
        when(mockAnotherPageDescriptor.getPageClass()).thenReturn((Class) YetAnotherPage.class);

        descriptorsToMethods = LinkedHashMultimap.create();
        when(mockUntraversedSubmitMethodFinder.getUntraversedSubmitMethods(routes, pageDescriptors))
                .thenReturn(descriptorsToMethods);
    }

    @Test
    public void appendingRequiredTraversalsLeavesRoutesUnmodifiedIfNoSuchTraversals() {
        // when
        List<Route> appendedRoutes = traversalAppender.appendTraversalsToRoutes(routes, pageDescriptors);

        // then
        assertThat(appendedRoutes, is(routes));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void untraversedSubmitMethodOnLeafPageIsAppendedToRoute() throws Exception {
        // given
        Method method = SomePage.class.getMethod("goToSomeOtherPage");
        descriptorsToMethods.put(mockPageDescriptor, method);
        when(mockRoute.getRootPageClass()).thenReturn((Class)SomePage.class);
        Route mockRouteClone = mock(Route.class);
        when(mockRoute.clone()).thenReturn(mockRouteClone);
        List<Route> expectedRoutes = new ArrayList<Route>();
        expectedRoutes.add(mockRouteClone);

        // when
        List<Route> appendedRoutes = traversalAppender.appendTraversalsToRoutes(routes, pageDescriptors);

        // then
        verify(mockRouteClone).appendTraversalByMethodToPageDescriptor(method, mockOtherPageDescriptor);
        assertThat(appendedRoutes, is(expectedRoutes));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void multipleUntraversedSubmitMethodsOnLeafPageResultsInMultipleAppendedRoutes() throws Exception {
        // given
        Method method = SomePage.class.getMethod("goToSomeOtherPage");
        Method anotherMethod = SomePage.class.getMethod("goToYetAnotherPage");
        descriptorsToMethods.put(mockPageDescriptor, method);
        descriptorsToMethods.put(mockPageDescriptor, anotherMethod);
        when(mockRoute.getRootPageClass()).thenReturn((Class)SomePage.class);
        Route mockRouteClone = mock(Route.class);
        Route mockAnotherRouteClone = mock(Route.class);
        when(mockRoute.clone()).thenReturn(mockRouteClone, mockAnotherRouteClone);
        List<Route> expectedRoutes = new ArrayList<Route>();
        expectedRoutes.add(mockRouteClone);
        expectedRoutes.add(mockAnotherRouteClone);

        // when
        List<Route> appendedRoutes = traversalAppender.appendTraversalsToRoutes(routes, pageDescriptors);

        // then
        verify(mockRouteClone).appendTraversalByMethodToPageDescriptor(method, mockOtherPageDescriptor);
        verify(mockAnotherRouteClone).appendTraversalByMethodToPageDescriptor(anotherMethod, mockAnotherPageDescriptor);
        assertThat(appendedRoutes, is(expectedRoutes));
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class SomePage {
        public SomeOtherPage goToSomeOtherPage() { return null; }
        public YetAnotherPage goToYetAnotherPage() { return null; }
    }

    private static class SomeOtherPage {}

    private static class YetAnotherPage {}
}
