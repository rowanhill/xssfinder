package org.xssfinder.runner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.xssfinder.xss.XssAttack;
import org.xssfinder.xss.XssGenerator;

import java.util.*;

/**
 * A DriverWrapper backed by an HtmlUnitDriver
 */
public class DefaultHtmlUnitDriverWrapper implements DriverWrapper {
    private final WebDriverPageInstantiator pageInstantiator;
    private HtmlUnitDriver driver;

    public DefaultHtmlUnitDriverWrapper() {
        driver = createDriver();
        pageInstantiator = new WebDriverPageInstantiator(driver);
    }

    @Override
    public PageInstantiator getPageInstantiator() {
        return pageInstantiator;
    }

    @Override
    public void visit(String url) {
        driver.get(url);
    }

    @Override
    public Set<String> getCurrentXssIds() {
        Object result = driver.executeScript("return window.xssfinder");
        //noinspection unchecked
        return result != null ? new HashSet<String>((ArrayList<String>)result) : new HashSet<String>();
    }

    @Override
    public int getFormCount() {
        return driver.findElements(By.xpath("//form")).size();
    }

    @Override
    public Map<String, String> putXssAttackStringsInInputs(XssGenerator xssGenerator) {
        List<WebElement> elements = driver.findElements(
                By.cssSelector("input[type=text],input[type=search],input[type=password],textarea"));
        Map<String, String> inputsToAttacks = new HashMap<String, String>();
        WebDriverXPathFinder xpathFinder = new WebDriverXPathFinder();
        for (WebElement element : elements) {
            XssAttack xssAttack = xssGenerator.createXssAttack();
            element.sendKeys(xssAttack.getAttackString());
            inputsToAttacks.put(xpathFinder.getXPath(element), xssAttack.getIdentifier());
        }
        return inputsToAttacks;
    }

    @Override
    public void renewSession() {
        driver.close();
        driver = createDriver();
        pageInstantiator.setDriver(driver);
    }

    private HtmlUnitDriver createDriver() {
        return new HtmlUnitDriver(true);
    }

}
