package org.xssfinder.runner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class DefaultHtmlUnitDriverWrapperTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Test
    public void createsWebDriverPageInstantiator() {
        // given
        DefaultHtmlUnitDriverWrapper driverWrapper = new DefaultHtmlUnitDriverWrapper();

        // when
        PageInstantiator pageInstantiator = driverWrapper.getPageInstantiator();

        // then
        assertThat(pageInstantiator, is(instanceOf(WebDriverPageInstantiator.class)));
    }

    @Test
    public void visitMakesRequestToUrl() {
        // given
        DefaultHtmlUnitDriverWrapper driverWrapper = new DefaultHtmlUnitDriverWrapper();

        // when
        driverWrapper.visit("http://localhost:8089/");

        // then
        verify(getRequestedFor(urlEqualTo("/")));
    }
}
