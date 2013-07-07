package org.xssfinder.testsite.simple.traverser;

import org.xssfinder.CustomSubmitter;
import org.xssfinder.LabelledXssGenerator;
import org.xssfinder.runner.UntraversableException;
import org.xssfinder.testsite.simple.page.HomePage;
import org.xssfinder.testsite.simple.page.LoginPage;

public class LoginPageSubmitter implements CustomSubmitter {
    @Override
    public HomePage submit(Object page, LabelledXssGenerator xssGenerator) {
        if (!(page instanceof LoginPage)) {
            throw new UntraversableException(page.toString() + " was not instance of LoginPage");
        }
        LoginPage loginPage = (LoginPage)page;
        return loginPage.logInAs(
                xssGenerator.getXssAttackTextForLabel("username"),
                xssGenerator.getXssAttackTextForLabel("password")
        );
    }
}
