package org.xssfinder.routing;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PageDescriptorTest {

    @Test
    public void exposesPageClass() {
        // given
        PageDescriptor descriptor = new PageDescriptor(OrdinaryPage.class);

        // when
        Class<?> pageClass = descriptor.getPageClass();

        // then
        Class<?> ordinaryPageClass = OrdinaryPage.class;
        assertThat(pageClass == ordinaryPageClass, is(true));
    }

    @Test
    public void ordinaryPageIsNotARoot() {
        // given
        PageDescriptor descriptor = new PageDescriptor(OrdinaryPage.class);

        // when
        boolean isRoot = descriptor.isRoot();

        // then
        assertThat(isRoot, is(false));
    }

    @Test
    public void startPageIsARoot() {
        // given
        PageDescriptor descriptor = new PageDescriptor(StartPage.class);

        // when
        boolean isRoot = descriptor.isRoot();

        // then
        assertThat(isRoot, is(true));
    }

    @Test
    public void linkedPagesIsEmptyForLeafPage() {
        // given
        PageDescriptor descriptor = new PageDescriptor(OrdinaryPage.class);

        // when
        Set<Class<?>> linkedPages = descriptor.getLinkedPages();

        // then
        Set<Class<?>> emptySet = ImmutableSet.of();
        assertThat(linkedPages, is(emptySet));
    }

    @Test
    public void linkedPagesHasPagesReturnedByMethodsForNonLeafPage() {
        // given
        PageDescriptor descriptor = new PageDescriptor(StartPage.class);

        // when
        Set<Class<?>> linkedPages = descriptor.getLinkedPages();

        // then
        Set<Class<?>> expectedPages = new HashSet<Class<?>>();
        expectedPages.add(OrdinaryPage.class);
        assertThat(linkedPages, is(expectedPages));
    }

    @Page
    private class OrdinaryPage {}

    @Page
    @CrawlStartPoint
    private class StartPage {
        public OrdinaryPage goToOrdinaryPage() { return null; }
    }
}
