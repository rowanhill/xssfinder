package org.xssfinder.runner;

import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;

/**
 * Creates page objects backed by WebDriver implementations
 */
public class WebDriverPageInstantiator implements PageInstantiator {
    private WebDriver driver;

    public WebDriverPageInstantiator(WebDriver driver) {
        this.driver = driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public <T> T instantiatePage(Class<T> pageClass) {
        try {
            Constructor<T> constructor = pageClass.getDeclaredConstructor(WebDriver.class);
            constructor.setAccessible(true);
            return constructor.newInstance(driver);
        } catch(Exception e) {
            throw new PageInstantiationException(e);
        }
    }
}
