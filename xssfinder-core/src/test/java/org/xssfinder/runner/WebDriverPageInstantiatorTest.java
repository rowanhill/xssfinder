package org.xssfinder.runner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class WebDriverPageInstantiatorTest {

    @Mock
    private WebDriver mockWebDriver;
    @InjectMocks
    private WebDriverPageInstantiator instantiator;

    @Test
    public void instantiatesClassWithConstructorThatTakesWebDriver() {
        // when
        WebDriverPage page = instantiator.instantiatePage(WebDriverPage.class);

        // then
        assertThat(page, is(not(nullValue())));
        assertThat(page.driver, is(mockWebDriver));
    }

    @Test(expected=PageInstantiationException.class)
    public void throwsExceptionWhenInstantiatingNonWebDriverClass() throws Exception {
        // when
        instantiator.instantiatePage(NonWebDriverPage.class);
    }

    private static class WebDriverPage {
        public WebDriver driver;
        public WebDriverPage(WebDriver driver) {
            this.driver = driver;
        }
    }

    private static class NonWebDriverPage { }
}
