package app;

import controllers.pageobjects.LoginPage;
import org.junit.Before;

public abstract class AbstractLoggedInBrowserApp extends AbstractBrowserApp {

    @Before
    public void logIn() {
        LoginPage loginPage = new LoginPage(driver);
        browser.goTo(loginPage);
        loginPage.fillAndSubmitForm();
    }
}
