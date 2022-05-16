package org.uludag.bmb.controller.database;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.sqlite.SQLiteDataSource;
import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.beans.dataproperty.TableViewDataProperty;

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

    public List<FileRecord> getAllRecords() {
        ResultSetHandler<List<FileRecord>> rsh = new BeanListHandler<FileRecord>(FileRecord.class);
        try {
            List<FileRecord> records = this.queryRunner
                    .query(this.query, rsh);
            return records;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getNotifications() {
        try {
            Statement statement = this.conn.createStatement();
            String query = "Select * From notifications";
            ResultSet rst = statement.executeQuery(query);
            List<String> notifications = new ArrayList<String>();
            while (rst.next()) {
                notifications.add(rst.getString("message"));
            }
            query = "Delete From notifications";
            statement.execute(query);
            return notifications;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<TableViewDataProperty> getTreeCache() {
        ResultSetHandler<List<TableViewDataProperty>> rsh = new BeanListHandler<TableViewDataProperty>(
                TableViewDataProperty.class);
        try {
            List<TableViewDataProperty> cache = this.queryRunner
                    .query("SELECT * FROM treecache", rsh);
            return cache;
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

    public void createTreeCacheTable() {
        String query = "CREATE TABLE IF NOT EXISTS treecache " +
                "(" +
                "id integer PRIMARY KEY," +
                "filePath TEXT NOT NULL," +
                "fileName TEXT NOT NULL," +
                "lastEditDate TEXT NOT NULL," +
                "syncStatus BOOLEAN NOT NULL CHECK(syncStatus IN(0,1))" +
                ")";
        try {
            PreparedStatement statement = this.conn.prepareStatement(query);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertTreeCache(TableViewDataProperty dp) {
        String query = "INSERT INTO treecache (filePath, fileName, lastEditDate, syncStatus) values(?, ?, ?, ?)";
        try {
            PreparedStatement statement = this.conn.prepareStatement(query);
            statement.setString(1, dp.filePath().get());
            statement.setString(2, dp.getFileName());
            statement.setString(3, new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(dp.getLastEditDate()));
            statement.setBoolean(4, dp.getSyncStatus());

            statement.execute();
        } catch (Exception e) {
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
            statement.setString(4, fr.getModificationDate());
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
