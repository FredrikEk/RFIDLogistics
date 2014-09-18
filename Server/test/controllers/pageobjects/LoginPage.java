package controllers.pageobjects;

import org.fluentlenium.core.FluentPage;
import org.openqa.selenium.WebDriver;

import static org.fest.assertions.Assertions.assertThat;

public class LoginPage extends FluentPage {

    private static final String URL = "http://localhost:8000/login";

    public static final String CORRECT_USERNAME =  "admin@smartrfid.se";

    public static final String CORRECT_PASSWORD =  "a1b2c3";

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    @Override
    public void isAt() {
        assertThat(url()).isEqualTo(URL);
    }

    public void fillAndSubmitForm(String... orderedParams) {
        fill("input").with(orderedParams);
        submit("input[type='submit']");
    }

    public void fillAndSubmitForm() {
        fillAndSubmitForm(CORRECT_USERNAME, CORRECT_PASSWORD);
    }
}
