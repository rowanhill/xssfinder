package org.xssfinder.runner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xssfinder.AfterRoute;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.reflection.*;
import org.xssfinder.reflection.InstantiationException;
import org.xssfinder.remote.MethodDefinition;
import org.xssfinder.remote.PageDefinition;
import org.xssfinder.remote.TLifecycleEventHandlerException;
import org.xssfinder.remote.TraversalMode;
import org.xssfinder.scanner.ThriftToReflectionLookup;
import org.xssfinder.xss.XssGenerator;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorContextTest {
    private static final String HOME_PAGE_URL = "http://home";
    private static final String HOME_PAGE_ID = "HomePage";

    @Mock
    private DriverWrapper mockDriverWrapper;
    @Mock
    private XssGenerator mockXssGenerator;
    @Mock
    private PageTraverser mockPageTraverser;
    @Mock
    private PageInstantiator mockPageInstantiator;
    @Mock
    private Instantiator mockInstantiator;
    @Mock
    private LifecycleEventExecutor mockLifecycleEventExecutor;

    @Mock
    private HomePage mockHomePage;
    @Mock
    private PageDefinition mockHomePageDefinition;
    private ThriftToReflectionLookup lookup;

    private Set<MethodDefinition> homePageMethods = new HashSet<MethodDefinition>();

    private ExecutorContext context;

    @Before
    public void setUp() {
        when(mockHomePageDefinition.getIdentifier()).thenReturn(HOME_PAGE_ID);
        when(mockHomePageDefinition.getMethods()).thenReturn(homePageMethods);
        when(mockDriverWrapper.getPageInstantiator()).thenReturn(mockPageInstantiator);
        when(mockPageInstantiator.instantiatePage(HomePage.class)).thenReturn(mockHomePage);
        lookup = new ThriftToReflectionLookup();
        lookup.putPageClass(HOME_PAGE_ID, HomePage.class);
        context = new ExecutorContext(mockDriverWrapper, mockXssGenerator, mockPageTraverser, mockInstantiator, mockLifecycleEventExecutor);
        context.setThriftToReflectionLookup(lookup);
    }

    @Test
    public void visitingUrlOfRootPageIsDelegatedToDriverWrapper() {
        // when
        context.visitUrlOfRootPage(HOME_PAGE_ID);

        // then
        verify(mockDriverWrapper).visit(HOME_PAGE_URL);
    }

    @Test
    public void visitingRootPageInstantiatesPage() {
        // when
        context.visitUrlOfRootPage(HOME_PAGE_ID);

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
        MethodDefinition mockMethodDefinition = mockMethodDefinition(method);
        TraversalResult mockTraversalResult = mock(TraversalResult.class);
        when(mockPageTraverser.traverse(mockHomePage, method, TraversalMode.NORMAL)).thenReturn(mockTraversalResult);
        visitHomePage();

        // when
        TraversalResult traversalResult = context.traverseMethod(mockMethodDefinition, TraversalMode.NORMAL);

        // then
        assertThat(traversalResult, is(mockTraversalResult));
    }

    @Test
    public void traversingASecondTimeTraversesFromResultingPageObjectOfFirstTraversal() throws Exception {
        // given
        Method goToSecondPage = HomePage.class.getMethod("goToSecondPage");
        MethodDefinition mockGoToSecondPageDef = mockMethodDefinition(goToSecondPage);
        Method goToThirdPage = SecondPage.class.getMethod("goToThirdPage");
        MethodDefinition mockGoToThirdPageDef = mockMethodDefinition(goToThirdPage);
        TraversalResult mockTraversalResult1 = mock(TraversalResult.class);
        when(mockPageTraverser.traverse(mockHomePage, goToSecondPage, TraversalMode.NORMAL))
                .thenReturn(mockTraversalResult1);
        SecondPage mockSecondPage = mock(SecondPage.class);
        when(mockTraversalResult1.getPage()).thenReturn(mockSecondPage);
        TraversalResult mockTraversalResult2 = mock(TraversalResult.class);
        when(mockPageTraverser.traverse(mockSecondPage, goToThirdPage, TraversalMode.NORMAL))
                .thenReturn(mockTraversalResult2);
        visitHomePage();

        // when
        context.traverseMethod(mockGoToSecondPageDef, TraversalMode.NORMAL);
        TraversalResult traversalResult = context.traverseMethod(mockGoToThirdPageDef, TraversalMode.NORMAL);

        // then
        assertThat(traversalResult, is(mockTraversalResult2));
    }


    @Test
    public void invokingAfterRouteHandlerDelegatesToLifecycleEventExecutor() throws Exception {
        // given
        HomeLifecycleHandler mockLifecycleHandler = mock(HomeLifecycleHandler.class);
        when(mockInstantiator.instantiate(HomeLifecycleHandler.class)).thenReturn(mockLifecycleHandler);

        // when
        context.invokeAfterRouteHandler(HOME_PAGE_ID);

        // then
        verify(mockLifecycleEventExecutor).afterRoute(mockLifecycleHandler, null);
    }

    @Test
    public void invokingAfterRouteHandlerPassesCurrentPageToLifecycleEventExecutor() throws Exception {
        // given
        HomeLifecycleHandler mockLifecycleHandler = mock(HomeLifecycleHandler.class);
        when(mockInstantiator.instantiate(HomeLifecycleHandler.class)).thenReturn(mockLifecycleHandler);
        context.visitUrlOfRootPage(HOME_PAGE_ID);

        // when
        context.invokeAfterRouteHandler(HOME_PAGE_ID);

        // then
        verify(mockLifecycleEventExecutor).afterRoute(mockLifecycleHandler, mockHomePage);
    }

    @Test(expected=TLifecycleEventHandlerException.class)
    public void invokingAfterRouteHandlerThrowsExceptionIfCreatingLifecycleHandlerFails() throws Exception {
        // given
        when(mockInstantiator.instantiate(HomeLifecycleHandler.class)).thenThrow(new InstantiationException(null));

        // when
        context.invokeAfterRouteHandler(HOME_PAGE_ID);
    }

    @Test
    public void invokingAfterRouteHandlerDoesNothingIfRootPageClassSpecifiesNoHandler() throws Exception {
        // given
        lookup.putPageClass(HOME_PAGE_ID, HomePageWithoutLifecycleHandler.class);

        // when
        context.invokeAfterRouteHandler(HOME_PAGE_ID);

        // then
        //noinspection unchecked
        verify(mockInstantiator, never()).instantiate(Matchers.any(Class.class));
        verify(mockLifecycleEventExecutor, never()).afterRoute(any(Object.class), any(Object.class));
    }

    @Test
    public void renewingSessionIsDelegatedToDriverWrapper() {
        // when
        context.renewSession();

        // then
        verify(mockDriverWrapper).renewSession();
    }

    @Test(expected=NullPointerException.class)
    public void pageIsSetToNullAfterRenewingSession() throws Exception {
        // given
        visitHomePage();
        context.renewSession();
        Method method = HomePage.class.getMethod("goToSecondPage");
        MethodDefinition mockMethodDefinition = mockMethodDefinition(method);
        when(mockMethodDefinition.getIdentifier()).thenReturn("goToSecondPageId");
        lookup.putMethod("goToSecondPageId", method);
        NullPointerException npe = new NullPointerException();
        when(mockPageTraverser.traverse(isNull(), eq(method), eq(TraversalMode.NORMAL))).thenThrow(npe);

        try {
            // when
            context.traverseMethod(mockMethodDefinition, TraversalMode.NORMAL);

            // then
        } catch (NullPointerException e) {
            // We test that the exception we see raised is precisely the one we expected to throw, as other null
            // pointer exceptions can be thrown by traverseMethod
            assertThat(e, is(npe));
            throw e;
        }
    }

    private MethodDefinition mockMethodDefinition(Method method) {
        MethodDefinition mockMethodDefinition = mock(MethodDefinition.class);
        when(mockMethodDefinition.getIdentifier()).thenReturn(method.getName());
        lookup.putMethod(method.getName(), method);
        return  mockMethodDefinition;
    }

    private void visitHomePage() {
        context.visitUrlOfRootPage(HOME_PAGE_ID);
    }

    @CrawlStartPoint(url=HOME_PAGE_URL, lifecycleHandler=HomeLifecycleHandler.class)
    private static class HomePage {
        public SecondPage goToSecondPage() { return null; }
    }

    private static class SecondPage {
        @SuppressWarnings("UnusedDeclaration")
        public ThirdPage goToThirdPage() { return null; }
    }

    private static class ThirdPage {}

    private static class HomeLifecycleHandler {
        @AfterRoute
        public void foo(Object page) {

        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @CrawlStartPoint(url=HOME_PAGE_URL)
    private static class HomePageWithoutLifecycleHandler {}
}
