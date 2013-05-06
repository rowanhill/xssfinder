package org.xssfinder.runner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.xssfinder.xss.XssAttack;
import org.xssfinder.xss.XssGenerator;

import java.util.*;

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

}
