package org.xssfinder.runner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.routing.PageTraversal;
import org.xssfinder.routing.Route;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageContextFactoryTest {
    @Mock
    private PageTraverser mockPageTraverser;
    @Mock
    private PageInstantiator mockPageInstantiator;
    @Mock
    private Route mockRoute;
    @Mock
    private SomePage mockPage;
    @Mock
    private DriverWrapper mockDriverWrapper;
    @Mock
    private PageTraversal mockPageTraversal;

    @SuppressWarnings("unchecked")
    @Test
    public void constructsPageContexts() {
        // given
        PageContextFactory factory = new PageContextFactory(mockPageTraverser, mockPageInstantiator);
        when(mockRoute.getRootPageClass()).thenReturn((Class)SomePage.class);
        when(mockRoute.getPageTraversal()).thenReturn(mockPageTraversal);
        when(mockPageInstantiator.instantiatePage(SomePage.class)).thenReturn(mockPage);

        // when
        PageContext context = factory.createContext(mockDriverWrapper, mockRoute);

        // then
        verify(mockRoute).getRootPageClass();
        verify(mockRoute).getPageTraversal();
        verify(mockPageInstantiator).instantiatePage(SomePage.class);
        assertThat(context, is(notNullValue()));
    }

    private static class SomePage {}
}
