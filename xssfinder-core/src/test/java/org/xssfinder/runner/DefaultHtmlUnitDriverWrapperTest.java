package org.xssfinder.runner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.google.common.collect.ImmutableSet;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.xssfinder.xss.XssAttack;
import org.xssfinder.xss.XssGenerator;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultHtmlUnitDriverWrapperTest {
    private static final String INDEX_PAGE =
            "<html>\n" +
            "    <body>\n" +
            "        <form action=\"/submit\" method=\"post\">\n" +
            "            <input type=\"text\" name=\"text1\" />\n" +
            "            <input type=\"text\" name=\"text2\" />\n" +
            "            <input type=\"password\" name=\"password\" />\n" +
            "            <input type=\"search\" name=\"search\" />\n" +
            "            <textarea name=\"textarea\"></textarea>\n" +
            "            <input type=\"submit\" id=\"submit\" value=\"Submit\" />\n" +
            "        </form>\n" +
            "        <script type=\"text/javascript\">\n" +
            "            window.xssfinder = ['123', '456'];\n" +
            "        </script>\n" +
            "    </body>\n" +
            "</html>";
    private static final String TWO_FORM_PAGE =
            "<html>\n" +
                    "    <body>\n" +
                    "        <form action=\"/submit\" method=\"post\">\n" +
                    "        </form>\n" +
                    "        <form action=\"/submit\" method=\"post\">\n" +
                    "        </form>\n" +
                    "    </body>\n" +
                    "</html>";
    private static final String JSLESS_PAGE = "<html><body></body></html>";

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

    @Test
    public void xssAttackingPutsXssFromGeneratorInAllTextInputAndTextArea() throws Exception {
        // given
        DefaultHtmlUnitDriverWrapper driverWrapper = new DefaultHtmlUnitDriverWrapper();
        XssGenerator mockXssGenerator = mock(XssGenerator.class);
        XssAttack mockAttack = mock(XssAttack.class);
        when(mockAttack.getAttackString()).thenReturn("xss");
        when(mockXssGenerator.createXssAttack()).thenReturn(mockAttack);
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse().withBody(INDEX_PAGE))
        );
        driverWrapper.visit("http://localhost:8089/");

        // when
        driverWrapper.putXssAttackStringsInInputs(mockXssGenerator);
        clickSubmit(driverWrapper);

        // then
        List<LoggedRequest> requests = findAll(postRequestedFor(urlEqualTo("/submit")));
        assertThat(requests.size(), is(1));
        LoggedRequest request = requests.get(0);
        String body = request.getBodyAsString();
        Map<String,String> params = getParams(body);
        assertThat(params.size(), is(5));
        assertThat(params, hasEntry("text1", "xss"));
        assertThat(params, hasEntry("text2", "xss"));
        assertThat(params, hasEntry("search", "xss"));
        assertThat(params, hasEntry("password", "xss"));
        assertThat(params, hasEntry("textarea", "xss"));
    }

    @Test
    public void puttingInXssAttackStringsReturnsAttackedInputIdentifiers() {
        // given
        DefaultHtmlUnitDriverWrapper driverWrapper = new DefaultHtmlUnitDriverWrapper();
        XssGenerator mockXssGenerator = mock(XssGenerator.class);
        XssAttack mockAttack = mock(XssAttack.class);
        when(mockAttack.getAttackString()).thenReturn("xss");
        when(mockAttack.getIdentifier()).thenReturn("xssId");
        when(mockXssGenerator.createXssAttack()).thenReturn(mockAttack);
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse().withBody(INDEX_PAGE))
        );
        driverWrapper.visit("http://localhost:8089/");

        // when
        Map<String, String> inputsToXss = driverWrapper.putXssAttackStringsInInputs(mockXssGenerator);

        // then
        assertThat(inputsToXss, hasEntry("body/form[1]/input[1]", "xssId"));
        assertThat(inputsToXss, hasEntry("body/form[1]/input[2]", "xssId"));
        assertThat(inputsToXss, hasEntry("body/form[1]/input[3]", "xssId"));
        assertThat(inputsToXss, hasEntry("body/form[1]/input[4]", "xssId"));
        assertThat(inputsToXss, hasEntry("body/form[1]/textarea[1]", "xssId"));
    }

    @Test
    public void gettingXssIdsGetsValuesFromJsArrayVar() {
        // given
        DefaultHtmlUnitDriverWrapper driverWrapper = new DefaultHtmlUnitDriverWrapper();
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse().withBody(INDEX_PAGE))
        );
        driverWrapper.visit("http://localhost:8089/");

        // when
        Set<String> xssIds = driverWrapper.getCurrentXssIds();

        // then
        Set<String> expectedIds = ImmutableSet.of("123", "456");
        assertThat(xssIds, is(expectedIds));
    }

    @Test
    public void currentXssIdsIsEmptySetIfNotFoundOnPage() {
        // given
        DefaultHtmlUnitDriverWrapper driverWrapper = new DefaultHtmlUnitDriverWrapper();
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse().withBody(JSLESS_PAGE))
        );
        driverWrapper.visit("http://localhost:8089/");

        // when
        Set<String> xssIds = driverWrapper.getCurrentXssIds();

        // then
        Set<String> expectedIds = ImmutableSet.of();
        assertThat(xssIds, is(expectedIds));
    }

    @Test
    public void countsNumberOfFormsOnPage() {
        // given
        DefaultHtmlUnitDriverWrapper driverWrapper = new DefaultHtmlUnitDriverWrapper();
        stubFor(get(urlEqualTo("/zero"))
                .willReturn(aResponse().withBody(JSLESS_PAGE))
        );
        stubFor(get(urlEqualTo("/one"))
                .willReturn(aResponse().withBody(INDEX_PAGE))
        );
        stubFor(get(urlEqualTo("/two"))
                .willReturn(aResponse().withBody(TWO_FORM_PAGE))
        );

        // when
        driverWrapper.visit("http://localhost:8089/zero");
        int numFormsOnFormlessPage = driverWrapper.getFormCount();
        driverWrapper.visit("http://localhost:8089/one");
        int numFormsOnIndexPage = driverWrapper.getFormCount();
        driverWrapper.visit("http://localhost:8089/two");
        int numFormsOnTwoFormPage = driverWrapper.getFormCount();

        // then
        assertThat(numFormsOnFormlessPage, is(0));
        assertThat(numFormsOnIndexPage, is(1));
        assertThat(numFormsOnTwoFormPage, is(2));
    }

    private void clickSubmit(DefaultHtmlUnitDriverWrapper driverWrapper) throws Exception {
        Field driverField = DefaultHtmlUnitDriverWrapper.class.getDeclaredField("driver");
        driverField.setAccessible(true);
        WebDriver driver = (WebDriver)driverField.get(driverWrapper);
        driver.findElement(By.id("submit")).click();
    }

    private Map<String,String> getParams(String postRequestBody) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        StringTokenizer tokenizer = new StringTokenizer(postRequestBody, "&");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            String[] split = token.split("=");
            String name = URLDecoder.decode(split[0], "UTF-8");
            String value = split.length > 1 ? URLDecoder.decode(split[1], "UTF-8") : null;
            params.put(name, value);
        }
        return params;
    }
}
