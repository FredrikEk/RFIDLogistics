package controllers;

import app.AbstractBrowserApp;
import controllers.pageobjects.LoginPage;
import org.junit.Test;
import org.openqa.selenium.Cookie;

import static org.fest.assertions.Assertions.assertThat;

public class ApplicationTest extends AbstractBrowserApp {

    /**
     * Log in with correct username and password.
     */
    @Test
    public void logIn() {
        LoginPage loginPage = new LoginPage(driver);
        browser.goTo(loginPage);
        loginPage.isAt();
        assertThat(browser.getCookies().size()).isEqualTo(0);
        loginPage.fillAndSubmitForm();
        assertThat(browser.url()).isEqualTo("http://localhost:8000/");
        assertThat(browser.findFirst(".alert-success").getText()).isEqualTo(
                String.format("Successfully logged in as %s.", LoginPage.CORRECT_USERNAME));
        assertThat(browser.getCookies().size()).isEqualTo(1);
        assertThat(((Cookie)browser.getCookies().toArray()[0]).getName()).isEqualTo("PLAY_SESSION");
    }

    /**
     * Fail to log in with correct username but wrong password.
     */
    @Test
    public void failToLogInWithWrongPassword() {
        LoginPage loginPage = new LoginPage(driver);
        browser.goTo(loginPage);
        loginPage.isAt();
        loginPage.fillAndSubmitForm(LoginPage.CORRECT_USERNAME, "wrong password");
        loginPage.isAt();
        assertThat(browser.findFirst(".content li").getText()).isEqualTo("Wrong password.");
    }

    /**
     * Fail to log in with wrong username but correct password.
     */
    @Test
    public void failToLogInWithWrongUsername() {
        LoginPage loginPage = new LoginPage(driver);
        browser.goTo(loginPage);
        loginPage.isAt();
        loginPage.fillAndSubmitForm("wrong username", LoginPage.CORRECT_PASSWORD);
        loginPage.isAt();
        assertThat(browser.findFirst(".content li").getText()).isEqualTo(
                "A user with that email address doesn't exist.");
    }

    /**
     * Log in and then log out.
     */
    @Test
    public void logOut() {
        logIn();
        assertThat(((Cookie)browser.getCookies().toArray()[0]).getName()).isEqualTo("PLAY_SESSION");
        assertThat(browser.getCookies().size()).isEqualTo(1);
        browser.goTo("http://localhost:8000/logout");
        assertThat(browser.getCookies().size()).isEqualTo(0);
        browser.goTo("http://localhost:8000/");
    }

    /**
     * Check the index page when not logged in.
     */
    @Test
    public void showingCorrectIndexMessageWhenNotLoggedIn() {
        browser.goTo("http://localhost:8000/");
        assertThat(browser.findFirst(".page-header h1").getText()).isEqualTo(
                "Hello! Login to browse smart logistiksystem!");
    }

    /**
     * Check the index page when logged in.
     */
    @Test
    public void showingCorrectIndexMessageWhenLoggedIn() {
        logIn();
        browser.goTo("http://localhost:8000/");
        assertThat(browser.findFirst(".page-header h1").getText()).isEqualTo(
                "Welcome to the admin view of Smart Logistiksystem med RFID");
    }
}
