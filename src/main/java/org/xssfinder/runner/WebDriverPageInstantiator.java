package org.xssfinder.runner;

import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;

public class WebDriverPageInstantiator implements PageInstantiator {
    private final WebDriver driver;

    public WebDriverPageInstantiator(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public <T> Object instantiatePage(Class<T> pageClass) {
        try {
            Constructor<T> constructor = pageClass.getConstructor(WebDriver.class);
            return constructor.newInstance(driver);
        } catch(Exception e) {
            throw new PageInstantiationException(e);
        }
    }
}
