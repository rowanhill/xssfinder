package org.xssfinder.testsite.simple.lifecycle;

import org.xssfinder.AfterRoute;
import org.xssfinder.testsite.simple.page.HomePage;

public class LifecycleHandler {
    @AfterRoute
    public void afterRoute(HomePage homePage) throws Exception {
        homePage.logout();
    }
}
