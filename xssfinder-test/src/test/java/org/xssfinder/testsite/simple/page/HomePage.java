package org.xssfinder.testsite.simple.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;
import org.xssfinder.SubmitAction;

@Page
public class HomePage {
    private final WebDriver driver;

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    @SubmitAction
    public HomePage unsafeSubmit() {
        driver.findElement(By.id("unsafeSubmit")).click();
        return this;
    }

    @SubmitAction
    public HomePage safeSubmit() {
        driver.findElement(By.id("safeSubmit")).click();
        return this;
    }

    public LoginPage logout() {
        driver.findElement(By.linkText("log out")).click();
        return new LoginPage(driver);
    }
}
