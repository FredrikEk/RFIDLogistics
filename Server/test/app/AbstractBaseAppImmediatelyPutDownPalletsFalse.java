package app;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;

import static play.test.Helpers.start;
import static play.test.Helpers.stop;

/**
 * Extending this class will set up a FakeApplication server with a clean database before each test. The app will run in
 * its second mode where pallets aren't immediately put down on scanned, available/unoccupied pallet slots.
 */
public class AbstractBaseAppImmediatelyPutDownPalletsFalse extends AbstractApp {

    @BeforeClass
    public static void startApp() throws IOException {
        initApp(false);
        start(app);
        initSqlStatements();
    }

    @AfterClass
    public static void stopApp() {
        stop(app);
    }
}
