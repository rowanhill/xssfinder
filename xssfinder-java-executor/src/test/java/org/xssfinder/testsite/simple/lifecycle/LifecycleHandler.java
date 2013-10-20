package org.xssfinder.testsite.simple.lifecycle;

import org.xssfinder.testsite.simple.page.HomePage;
import org.xssfinder.AfterRoute;

public class LifecycleHandler {
    @AfterRoute
    public void afterRoute(HomePage homePage) throws Exception {
        homePage.logout();
    }
}
