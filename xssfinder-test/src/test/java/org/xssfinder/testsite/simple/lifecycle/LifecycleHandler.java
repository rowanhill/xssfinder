package org.xssfinder.testsite.simple.lifecycle;

import org.xssfinder.AfterRoute;
import org.xssfinder.runner.PageContext;
import org.xssfinder.testsite.simple.page.HomePage;

public class LifecycleHandler {
    @AfterRoute
    public void afterRoute(PageContext context) throws Exception {
        Object page = context.getPage();
        if (!(page instanceof HomePage)) {
            throw new Exception("Expected HomePage but got " + page.toString());
        }
        HomePage homePage = (HomePage)page;
        homePage.logout();
    }
}
