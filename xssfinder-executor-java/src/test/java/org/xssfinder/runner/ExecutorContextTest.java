package org.xssfinder.runner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.remote.TraversalMode;
import org.xssfinder.xss.XssGenerator;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorContextTest {
    private static final String HOME_PAGE_URL = "http://home";

    @Mock
    private DriverWrapper mockDriverWrapper;
    @Mock
    private XssGenerator mockXssGenerator;
    @Mock
    private PageTraverser mockPageTraverser;
    @Mock
    private PageInstantiator mockPageInstantiator;

    @Mock
    private HomePage mockHomePage;

    private ExecutorContext context;

    @Before
    public void setUp() {
        when(mockDriverWrapper.getPageInstantiator()).thenReturn(mockPageInstantiator);
        when(mockPageInstantiator.instantiatePage(HomePage.class)).thenReturn(mockHomePage);
        context = new ExecutorContext(mockDriverWrapper, mockXssGenerator, mockPageTraverser);
    }

    @Test
    public void visitingUrlOfRootPageIsDelegatedToDriverWrapper() {
        // given
        String pageId = "HomePage";
        Class<?> pageClass = HomePage.class;
        context.addPageMapping(pageId, pageClass);

        // when
        context.visitUrlOfRootPage(pageId);

        // then
        verify(mockDriverWrapper).visit(HOME_PAGE_URL);
    }

    @Test
    public void visitingRootPageInstantiatesPage() {
        // given
        String pageId = "HomePage";
        Class<?> pageClass = HomePage.class;
        context.addPageMapping(pageId, pageClass);

        // when
        context.visitUrlOfRootPage(pageId);

        // then
        verify(mockPageInstantiator).instantiatePage(HomePage.class);
    }

    @Test
    public void puttingXssAttackStringsInInputsIsDelegatedToDriverWrapper() throws Exception {
        // given
        Map<String, String> givenInputIdsToAttackIds = ImmutableMap.of("foo", "bar");
        when(mockDriverWrapper.putXssAttackStringsInInputs(mockXssGenerator)).thenReturn(givenInputIdsToAttackIds);

        // when
        Map<String, String> inputIdsToAttackIds = context.putXssAttackStringsInInputs();

        // then
        assertThat(inputIdsToAttackIds, is(givenInputIdsToAttackIds));
    }

    @Test
    public void gettingCurrentXssIdsIsDelegatedToDriverWrapper() throws Exception {
        // given
        Set<String> givenIds = ImmutableSet.of("foo");
        when(mockDriverWrapper.getCurrentXssIds()).thenReturn(givenIds);

        // when
        Set<String> xssIds = context.getCurrentXssIds();

        // then
        assertThat(xssIds, is(givenIds));
    }

    @Test
    public void gettingFormCountIsDelegatedToDriverWrapper() throws Exception {
        // given
        int givenFormCount = 3;
        when(mockDriverWrapper.getFormCount()).thenReturn(givenFormCount);

        // when
        int formCount = context.getFormCount();

        // then
        assertThat(formCount, is(givenFormCount));
    }

    @Test
    public void traversingMethodDelegatesToPageTraverser() throws Exception {
        // given
        Method method = HomePage.class.getMethod("goToSecondPage");
        TraversalResult mockTraversalResult = mock(TraversalResult.class);
        when(mockPageTraverser.traverse(mockHomePage, method, TraversalMode.NORMAL)).thenReturn(mockTraversalResult);
        visitHomePage();

        // when
        TraversalResult traversalResult = context.traverseMethod(method, TraversalMode.NORMAL);

        // then
        assertThat(traversalResult, is(mockTraversalResult));
    }

    private void visitHomePage() {
        String pageId = "HomePage";
        Class<?> pageClass = HomePage.class;
        context.addPageMapping(pageId, pageClass);
        context.visitUrlOfRootPage(pageId);
    }

    @CrawlStartPoint(url=HOME_PAGE_URL)
    private static class HomePage {
        public SecondPage goToSecondPage() { return null; }
    }

    private static class SecondPage {}
}
