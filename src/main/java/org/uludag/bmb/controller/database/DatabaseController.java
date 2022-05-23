package org.uludag.bmb.controller.database;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public FileRecord getByPathAndName(String path, String name) {
        ResultSetHandler<List<FileRecord>> rsh = new BeanListHandler<FileRecord>(FileRecord.class);
        try {
            List<FileRecord> records = this.queryRunner
                    .query(this.query + " WHERE path = '" + path + "' AND name = '" + name + "'", rsh);
            return records.get(0);
        } catch (Exception e) {
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

    public List<FileRecord> getRecordsByPath(String path) {
        ResultSetHandler<List<FileRecord>> rsh = new BeanListHandler<FileRecord>(FileRecord.class);
        try {
            List<FileRecord> records = this.queryRunner
                    .query(this.query + " WHERE path='" + path + "'", rsh);
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

    public final void deleteRecord(String fileName, String filePath) {
        String query = "DELETE FROM records WHERE name=? AND path=?";
        try {
            PreparedStatement statement = this.conn.prepareStatement(query);
            statement.setString(1, fileName);
            statement.setString(2, filePath);

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

    public FileRecord getByEncryptedNameAndPath(String encryptedName, String path) {
        ResultSetHandler<List<FileRecord>> rsh = new BeanListHandler<FileRecord>(FileRecord.class);
        try {
            List<FileRecord> records = this.queryRunner
                    .query(this.query + " WHERE path = '" + path + "' AND encryptedName = '" + encryptedName + "'",
                            rsh);
            if (records.size() != 0)
                return records.get(0);
            else
                return null;
        } catch (SQLException e) {
            return null;
        }
    }

    public void changeSyncStatus(TableViewDataProperty item, boolean b) {
        String query = "UPDATE records SET sync=? WHERE name=? AND path=?";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, item.getSync() ? 1 : 0);
            statement.setString(2, item.getFileName());
            statement.setString(3, item.getFilePath());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeSyncStatus(FileRecord item, boolean b) {
        String query = "UPDATE records SET sync=? WHERE name=? AND path=?";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, b ? 1 : 0);
            statement.setString(2, item.getName());
            statement.setString(3, item.getPath());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeChangeStatus(FileRecord item, boolean b) {
        String query = "UPDATE records SET changeStatus=? WHERE name=? AND path=?";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, b ? 1 : 0);
            statement.setString(2, item.getName());
            statement.setString(3, item.getPath());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
