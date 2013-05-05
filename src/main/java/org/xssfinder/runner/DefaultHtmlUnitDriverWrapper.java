package org.xssfinder.runner;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class DefaultHtmlUnitDriverWrapper implements DriverWrapper {
    private final WebDriver driver;

    public DefaultHtmlUnitDriverWrapper() {
        driver = new HtmlUnitDriver();
    }

    @Override
    public PageInstantiator getPageInstantiator() {
        return new WebDriverPageInstantiator(driver);
    }

    @Override
    public void visit(String url) {
        driver.get(url);
    }
}
