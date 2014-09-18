package app;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;

import static play.test.Helpers.start;
import static play.test.Helpers.stop;

/**
 * Extending this class will set up a FakeApplication server with a clean database before each test.
 */
public abstract class AbstractBaseApp extends AbstractApp {

    @BeforeClass
    public static void startApp() throws IOException {
        initApp();
        start(app);
        initSqlStatements();
    }

    @AfterClass
    public static void stopApp() {
        stop(app);
    }
}
