package org.xssfinder.testsite.simple.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.xssfinder.CrawlStartPoint;
import org.xssfinder.Page;
import org.xssfinder.TraverseWith;
import org.xssfinder.testsite.simple.lifecycle.LifecycleHandler;
import org.xssfinder.testsite.simple.traverser.LoginPageTraverser;

@Page
@CrawlStartPoint(url="http://localhost:8085/simple/", lifecycleHandler= LifecycleHandler.class)
public class LoginPage {
    private final WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    @TraverseWith(LoginPageTraverser.class)
    public HomePage logInAs(String username, String password) {
        driver.findElement(By.name("j_username")).sendKeys(username);
        driver.findElement(By.name("j_password")).sendKeys(password);
        driver.findElement(By.name("submit")).click();
        return new HomePage(driver);
    }
}
