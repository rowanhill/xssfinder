package org.xssfinder.testsite.simple.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;
import org.xssfinder.SubmitAction;

@Page
@CrawlStartPoint(url="http://localhost:8085/simple/")
public class HomePage {
    private final WebDriver driver;

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    @SubmitAction
    public HomePage submit() {
        driver.findElement(By.id("submit")).click();
        return this;
    }
}
