package org.xssfinder.runner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.xssfinder.xss.XssGenerator;

import java.util.List;

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

    @Override
    public void putXssAttackStringsInInputs(XssGenerator xssGenerator) {
        List<WebElement> elements = driver.findElements(
                By.cssSelector("input[type=text],input[type=search],input[type=password],textarea"));
        for (WebElement element : elements) {
            element.sendKeys(xssGenerator.createXssString());
        }
    }

}
