package app;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import play.test.Helpers;
import play.test.TestBrowser;
import play.test.TestServer;

import java.io.IOException;

import static play.test.Helpers.*;

/**
 * Extending this class will set up a FakeApplication server with a clean database and a test browser before each test.
 */
public abstract class AbstractBrowserApp extends AbstractApp {

    protected TestBrowser browser;

    protected WebDriver driver;

    private static TestServer testServer;

    /**
     * Start a test server at port 8000.
     */
    @BeforeClass
    public static void startTestServer() throws IOException {
        initApp();
        testServer = testServer(8000, app);
        start(testServer);
        initSqlStatements();
    }

    /**
     * Stop the test server.
     */
    @AfterClass
    public static void stopTestServer() {
        stop(testServer);
    }

    /**
     * Create a new browser before each test. This will clear things such as sessions and cookies.
     */
    @Before
    public void createBrowser() {
        browser = Helpers.testBrowser(HTMLUNIT);
        driver = browser.getDriver();
    }

    /**
     * Quit the browser when a test has finished.
     */
    @After
    public void test() {
        browser.quit();
    }
}
