package org.uludag.bmb.controller.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.sqlite.SQLiteDataSource;
import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.config.Config;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.controller.config.ConfigController;

public class DatabaseController {
    public Connection conn;
    public String url;
    public String dbName;
    public String tableName;
    private String query;
    private SQLiteDataSource ds;
    private QueryRunner queryRunner;

    public DatabaseController() {
        this.tableName = PropertiesReader.getProperty("dbRecordTable");
        this.dbName = PropertiesReader.getProperty("localDatabaseName");
        this.query = "SELECT * FROM " + this.tableName;
        this.url = getConnectionUrl(this.dbName);
        this.ds = new SQLiteDataSource();
        this.queryRunner = new QueryRunner(this.ds);
        this.ds.setUrl(this.getConnectionUrl(this.dbName));
        try {
            conn = DriverManager.getConnection(this.url);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<FileRecord> getByPathAndName(String path, String name) {
        ResultSetHandler<List<FileRecord>> rsh = new BeanListHandler<FileRecord>(FileRecord.class);
        try {
            List<FileRecord> records = this.queryRunner
                    .query(this.query + " WHERE path = '" + path + "' AND name = '" + name + "'", rsh);
            return records;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<FileRecord> getByEncryptedName(String encryptedName) {
        ResultSetHandler<List<FileRecord>> rsh = new BeanListHandler<FileRecord>(FileRecord.class);
        try {
            List<FileRecord> records = this.queryRunner
                    .query(this.query + " WHERE encryptedName = '" + encryptedName + "'", rsh);
            return records;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createRecordTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + this.tableName +
                "(" +
                "id integer PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "path TEXT NOT NULL," +
                "key TEXT NOT NULL," +
                "modificationDate TEXT NOT NULL," +
                "hash TEXT NOT NULL," +
                "encryptedName TEXT NOT NULL," +
                "sync BOOLEAN NOT NULL CHECK(sync IN(0, 1))" +
                ")";
        try {
            PreparedStatement statement = this.conn.prepareStatement(query);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createNotificationTable() {
        String query = "CREATE TABLE IF NOT EXISTS notifications " +
                "(" +
                "id integer PRIMARY KEY," +
                "message TEXT NOT NULL" +
                ")";
        try {
            PreparedStatement statement = this.conn.prepareStatement(query);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertNotification(String notificationMessage) {
        String query = "INSERT INTO notifications (message) values(?)";
        try {
            PreparedStatement statement = this.conn.prepareStatement(query);
            statement.setString(1, notificationMessage);

            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertRecord(FileRecord fr) {
        String query = "INSERT INTO " + this.tableName +
                "(name, path, key, modificationDate, hash, encryptedName, sync) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, fr.getName());
            statement.setString(2, fr.getPath());
            statement.setString(3, fr.getKey());
            statement.setString(4, fr.getEncryptedName());
            statement.setString(5, fr.getHash());
            statement.setString(6, fr.getEncryptedName());
            statement.setInt(7, fr.getSync());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private final String getConnectionUrl(String dbName) {
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
