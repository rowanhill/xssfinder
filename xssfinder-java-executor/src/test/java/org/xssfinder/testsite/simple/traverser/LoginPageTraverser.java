package org.xssfinder.testsite.simple.traverser;

import org.xssfinder.testsite.simple.page.HomePage;
import org.xssfinder.testsite.simple.page.LoginPage;
import org.xssfinder.CustomTraverser;
import org.xssfinder.runner.UntraversableException;

public class LoginPageTraverser implements CustomTraverser {
    @Override
    public HomePage traverse(Object page) {
        if (!(page instanceof LoginPage)) {
            throw new UntraversableException(page.toString() + " was not instance of LoginPage");
        }
        return ((LoginPage)page).logInAs("testuser", "strongpassword");
    }
}
