package org.uludag.bmb.controller.database;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.sqlite.SQLiteDataSource;
import org.uludag.bmb.PropertiesReader;

public class DatabaseController {
    public class Tables {
        public String record;
        public String notification;
        public String privateKey;
        public String publicInfo;
        public String sharedFilesKeyTable;

        public Tables() {
            this.record = PropertiesReader.getProperty("table_Record");
            this.notification = PropertiesReader.getProperty("table_Notification");
            this.privateKey = PropertiesReader.getProperty("table_privateKey");
            this.publicInfo = PropertiesReader.getProperty("table_publicInfo");
            this.sharedFilesKeyTable = PropertiesReader.getProperty("table_sharedKeyPeople");
        }
    }

    public class Databases {
        public String local;
        public String cloud;

        public Databases() {
            this.local = PropertiesReader.getProperty("database_Local");
            this.cloud = PropertiesReader.getProperty("database_Cloud");
        }
    }

    public Tables TABLES;
    public Databases DATABASES;

    private Connection localDb;
    private Connection cloudDb;
    private String connectionUrl;
    private SQLiteDataSource ds;
    private QueryRunner queryRunner;

    public Connection getConn() {
        return this.localDb;
    }

    public Connection getAzureCon() {
        return this.cloudDb;
    }

    public String getUrl() {
        return this.connectionUrl;
    }

    public SQLiteDataSource getDs() {
        return this.ds;
    }

    public QueryRunner getQueryRunner() {
        return this.queryRunner;
    }

    public DatabaseController() {
        this.TABLES = new Tables();
        this.DATABASES = new Databases();
        this.connectionUrl = getConnectionUrl(this.DATABASES.local);
        this.ds = new SQLiteDataSource();
        this.queryRunner = new QueryRunner(this.ds);
        this.ds.setUrl(this.connectionUrl);
        try {
            localDb = DriverManager.getConnection(this.connectionUrl);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            this.cloudDb = DriverManager.getConnection(this.DATABASES.cloud);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private final String getConnectionUrl(String dbName) {
        try {
            String dataDir = System.getProperty("user.dir");
            String os = System.getProperty("os.name").toLowerCase();

            if (os.indexOf("mac") >= 0) {
                dataDir += "/Data/";
            } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
                dataDir += "/Data/";
            } else {
                dataDir += "\\Data\\";
            }
            if (!Files.exists(Paths.get(dataDir))) {
                File fileFolder = new File(dataDir);
                fileFolder.mkdirs();
            }

            String url = "jdbc:sqlite:" + dataDir;

            url += dbName + ".db";

            return url;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}