package org.uludag.bmb.controller.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.config.Config;
import org.uludag.bmb.controller.config.ConfigController;

public class DatabaseController {
    public static final void createLocalDatabase() {
        String localDbName = PropertiesReader.getProperty("localDatabaseName");
        try {
            Connection connection = DriverManager.getConnection(getConnectionUrl(localDbName));
            DatabaseMetaData dbmeta = connection.getMetaData();
            System.out.println("The driver name is " + dbmeta.getDriverName());
            // TODO YEREL KAYIT OLUŞTURULDUĞUNA DAİR BİLDİRİM!
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getConnectionUrl(String dbName) {
        try {
            Config settings = ConfigController.Settings.LoadSettings();
            String localPath = settings.getLocalDropboxPath();
            String url = "jdbc:sqlite:" + localPath;
            String os = System.getProperty("os.name").toLowerCase();

            if (os.indexOf("mac") >= 0) {
                url += "/Data/";
            } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
                url += "/Data/";
            } else {
                url += "\\Data\\";
            }

            url += dbName + ".db";

            return url;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
