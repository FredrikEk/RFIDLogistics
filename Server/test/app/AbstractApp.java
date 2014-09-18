package app;

import com.avaje.ebean.Ebean;
import controllers.pageobjects.LoginPage;
import models.User;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import play.test.FakeApplication;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Based on: http://blog.matthieuguillermin.fr/2012/03/unit-testing-tricks-for-play-2-0-and-ebean/
 */
public abstract class AbstractApp {

    protected static FakeApplication app;
    private static String createSql = "";
    private static String dropSql = "";

    private enum Database {H2, MYSQL}

    /**
     * If MYSQL is the preferred mode it will fall back to H2 if there is no local process on port 3306.
     */
    private static final Database PREFERRED_MODE = Database.H2;

    /**
     * In H2 mode the testing suite won't use the triggers or views from `2.sql`, which will result in failures in some
     * the tests.
     *
     * In MYSQL mode it will run with a real, local MySQL database as a back-end. In this mode it will take advantage
     * of the triggers and views from `2.sql`. However, running with a real MySQL database might be significantly
     * slower than the H2 database.
     */
    private static Database mode;

    /**
     * Set up the app with the desired configuration.
     */
    protected static void initApp() {
        initApp(true);
    }

    /**
     * Set up the app with the desired configuration.
     *
     * @param immediatelyPutDownPallets If a scanning an unoccupied slot should result in immediately putting down the
     *                                  truck's lifted pallet.
     */
    protected static void initApp(boolean immediatelyPutDownPallets) {
        switch (PREFERRED_MODE) {
            case H2:
                mode = Database.H2;
                break;
            case MYSQL:
                try {
                    ServerSocket s = new ServerSocket(3306);
                    s.close();
                    mode = Database.H2;
                } catch (IOException e) {
                    if (e.getMessage().equals("Address already in use")) {
                        // I.e. a local MySQL database is running
                        mode = Database.MYSQL;
                    }
                }
                break;
        }
        Map<String, String> config = new HashMap<>();
        switch (mode) {
            case H2:
                config.putAll(inMemoryDatabase("default"));
                break;
            case MYSQL:
                config.put("db.default.driver", "com.mysql.jdbc.Driver");
                config.put("db.default.url",
                        "jdbc:mysql://localhost/kandidat?characterEncoding=UTF-8&allowMultiQueries=true");
                config.put("db.default.user", "admin");
                config.put("db.default.password", "password");
                config.put("immediately-put-down-pallets", Boolean.toString(immediatelyPutDownPallets));
                break;
        }
        config.put("evolutionplugin", "disabled");
        app = fakeApplication(config);
    }

    /**
     * Initialize the attributes createSql and dropSql.
     *
     * @throws IOException
     */
    protected static void initSqlStatements() throws IOException {
        // Read the evolution file

        String evolutionContent = null;
        switch (mode) {
            case H2:
                evolutionContent = FileUtils.readFileToString(app.getWrappedApplication().getFile(
                        "conf/evolutions/test/1.sql"));
                break;
            case MYSQL:
                evolutionContent = FileUtils.readFileToString(app.getWrappedApplication().getFile(
                        "conf/evolutions/default/1.sql")).replace("drop table", "drop table if exists");
                break;
        }

        // Split the String to get Create & Drop SQL
        String[] splittedEvolutionContent = evolutionContent.split("# --- !Ups");
        String[] upsDowns = splittedEvolutionContent[1].split("# --- !Downs");
        createSql = upsDowns[0];
        dropSql = upsDowns[1];

        // If running in MYSQL mode, also take advantage of the triggers and views
        if (mode == Database.MYSQL) {
            evolutionContent = FileUtils.readFileToString(
                    app.getWrappedApplication().getFile("conf/evolutions/default/2.sql")).replace(";;", ";");

            // Split the String to get Create & Drop SQL
            splittedEvolutionContent = evolutionContent.split("# --- !Ups");
            upsDowns = splittedEvolutionContent[1].split("# --- !Downs");
            createSql += upsDowns[0];
            dropSql += upsDowns[1];
        }
    }

    /**
     * Reset the database before a test is being run.
     */
    @Before
    public void createCleanDb() {
        Ebean.execute(Ebean.createCallableSql(dropSql));
        Ebean.execute(Ebean.createCallableSql(createSql));
        User admin = new User(LoginPage.CORRECT_USERNAME, "Admin", LoginPage.CORRECT_PASSWORD);
        admin.save();
    }

}
