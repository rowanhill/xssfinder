package org.xssfinder.runner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Creates uniquely identifying XPath expressions for WebDriver WebElements
 */
class WebDriverXPathFinder {
    public String getXPath(WebElement element) {
        if (element.getAttribute("id") != null) {
            return "//" + element.getTagName().toLowerCase() + "[@id=\"" + element.getAttribute("id") + "\"]";
        } else if (element.getTagName().equalsIgnoreCase("body")) {
            return "body";
        }

        int nodeCount = 1;
        WebElement parentElement = element.findElement(By.xpath(".."));
        List<WebElement> childElements = parentElement.findElements(By.xpath("./*"));
        for (WebElement childElement : childElements) {
            if (childElement.equals(element)) {
                return getXPath(parentElement) + "/" + element.getTagName().toLowerCase() + "[" + nodeCount + "]";
            }
            if (childElement.getTagName().equalsIgnoreCase(element.getTagName())) {
                nodeCount++;
            }
        }
        throw new RuntimeException("Element is not a child of its parent");
    }
}
