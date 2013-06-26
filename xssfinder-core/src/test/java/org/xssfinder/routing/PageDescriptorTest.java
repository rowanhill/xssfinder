package org.xssfinder.routing;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;
import org.xssfinder.SubmitAction;

import java.lang.reflect.Method;
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
    public void traversalMethodsIsEmptyForLeafPage() {
        // given
        PageDescriptor descriptor = new PageDescriptor(OrdinaryPage.class);

        // when
        Set<Method> traversalMethods = descriptor.getTraversalMethods();

        // then
        Set<Method> emptySet = ImmutableSet.of();
        assertThat(traversalMethods, is(emptySet));
    }

    @Test
    public void traversalMethodsHasPagesReturnedByMethodsForNonLeafPage() throws Exception {
        // given
        PageDescriptor descriptor = new PageDescriptor(StartPage.class);

        // when
        Set<Method> traversalMethods = descriptor.getTraversalMethods();

        // then
        Set<Method> expectedPages = new HashSet<Method>();
        expectedPages.add(StartPage.class.getMethod("goToOrdinaryPage"));
        assertThat(traversalMethods, is(expectedPages));
    }

    @Test
    public void submitMethodsIsEmptyForLeafNode() throws Exception {
        // given
        PageDescriptor descriptor = new PageDescriptor(OrdinaryPage.class);

        // when
        Set<Method> submitMethods = descriptor.getSubmitMethods();

        // then
        Set<Method> emptySet = ImmutableSet.of();
        assertThat(submitMethods, is(emptySet));
    }

    @Test
    public void submitMethodsContainsSubmitMethodButNotOtherTraversals() throws Exception {
        // given
        PageDescriptor descriptor = new PageDescriptor(SubmittablePage.class);

        // when
        Set<Method> submitMethods = descriptor.getSubmitMethods();

        // then
        Set<Method> expectedPages = new HashSet<Method>();
        expectedPages.add(SubmittablePage.class.getMethod("submitToOrdinaryPage"));
        assertThat(submitMethods, is(expectedPages));
    }

    @Test
    public void crawlStartPointUrlIsAvailable() {
        // given
        PageDescriptor descriptor = new PageDescriptor(StartPage.class);

        // when
        String url = descriptor.getCrawlStartPointUrl();

        // then
        assertThat(url, is("http://somehost/someurl"));
    }

    @Test(expected=NotAStartPointException.class)
    public void gettingCrawlStartPointUrlOfNonStartPointThrowsException() throws Exception {
        // given
        PageDescriptor descriptor = new PageDescriptor(OrdinaryPage.class);

        // when
        String url = descriptor.getCrawlStartPointUrl();
    }

    @Page
    private static class OrdinaryPage {}

    @Page
    @CrawlStartPoint(url="http://somehost/someurl")
    private static class StartPage {
        public OrdinaryPage goToOrdinaryPage() { return null; }
    }

    @Page
    private static class SubmittablePage {
        public OrdinaryPage goToOrdinaryPage() { return null; }

        @SubmitAction
        public OrdinaryPage submitToOrdinaryPage() { return null; }
    }
}
