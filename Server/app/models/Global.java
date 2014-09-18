package models;

import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        if (!app.isTest()) {
            // Check if the database is empty
            if (User.find.findRowCount() == 0) {
                User admin = new User("admin@smartrfid.se", "Admin", "a1b2c3");
                admin.save();
            }
            if (User.find.findRowCount() == 1) {
                User admin = new User("admin2@smartrfid.se", "Admin", "a1b2c3");
                admin.save();
            }
        }
    }
}