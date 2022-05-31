package org.uludag.bmb.operations.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.uludag.bmb.beans.crypto.FilePreview;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.beans.database.SharedFile;
import org.uludag.bmb.beans.dataproperty.CloudFileProperty;
import org.uludag.bmb.controller.database.DatabaseController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FileRecordOperations {
    private DatabaseController databaseController;

    public FileRecordOperations() {
        this.databaseController = new DatabaseController();
    }

    public void DELETE(String fileName, String filePath) {
        String query = "DELETE FROM records WHERE name=? AND path=?";
        try {
            PreparedStatement statement = this.databaseController.getConn().prepareStatement(query);
            statement.setString(1, fileName);
            statement.setString(2, filePath);

            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FileRecord getByPathAndName(String filePath, String fileName) {
        ResultSetHandler<List<FileRecord>> rsh = new BeanListHandler<FileRecord>(FileRecord.class);
        try {
            List<FileRecord> records = this.databaseController.getLocalQueryRunner()
                    .query("SELECT * FROM " + this.databaseController.TABLES.record
                            + " WHERE path = '" + filePath + "' AND name = '"
                            + fileName + "'", rsh);
            return records.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public List<FileRecord> getByEncryptedName(String encryptedName) {
        ResultSetHandler<List<FileRecord>> rsh = new BeanListHandler<FileRecord>(FileRecord.class);
        try {
            List<FileRecord> records = this.databaseController.getLocalQueryRunner()
                    .query("SELECT * FROM " + this.databaseController.TABLES.record + " WHERE encryptedName = '"
                            + encryptedName + "'", rsh);
            return records;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<FileRecord> getAll() {
        ResultSetHandler<List<FileRecord>> rsh = new BeanListHandler<FileRecord>(FileRecord.class);
        try {
            List<FileRecord> records = this.databaseController.getLocalQueryRunner()
                    .query("SELECT * FROM " + this.databaseController.TABLES.record, rsh);
            return records;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ObservableList<CloudFileProperty> getByPath(String path) {
        ResultSetHandler<List<FileRecord>> rsh = new BeanListHandler<FileRecord>(FileRecord.class);
        try {
            List<FileRecord> records = this.databaseController.getLocalQueryRunner()
                    .query("SELECT * FROM " + this.databaseController.TABLES.record + " WHERE path='" + path + "'",
                            rsh);
            ObservableList<CloudFileProperty> fileList = FXCollections.observableArrayList();
            for (FileRecord record : records) {
                fileList.add(new CloudFileProperty(record.getDownloadStatus(), record.getName(),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(record.getModificationDate()),
                        record.getPath(), record.getSync(), record.getChangeStatus()));
            }
            return fileList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void INSERT(FileRecord fr) {
        String query = "INSERT INTO " + this.databaseController.TABLES.record +
                "(name, path, key, modificationDate, hash, encryptedName, sync, changeStatus, downloadStatus) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = this.databaseController.getConn().prepareStatement(query);
            statement.setString(1, fr.getName());
            statement.setString(2, fr.getPath());
            statement.setString(3, fr.getKey());
            statement.setString(4, fr.getModificationDate());
            statement.setString(5, fr.getHash());
            statement.setString(6, fr.getEncryptedName());
            statement.setInt(7, fr.getSync());
            statement.setInt(8, fr.getChangeStatus());
            statement.setInt(9, fr.getDownloadStatus());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public FileRecord getByPathAndEncryptedName(String path, String encryptedName) {
        ResultSetHandler<List<FileRecord>> rsh = new BeanListHandler<FileRecord>(FileRecord.class);
        try {
            List<FileRecord> records = this.databaseController.getLocalQueryRunner()
                    .query("SELECT * FROM " + this.databaseController.TABLES.record
                            + " WHERE path = '"
                            + path
                            + "' AND encryptedName = '"
                            + encryptedName
                            + "'",
                            rsh);
            if (records.size() != 0)
                return records.get(0);
            else
                return null;
        } catch (SQLException e) {
            return null;
        }
    }

    public void UPDATE_SYNC_STATUS(String filePath, String fileName, boolean newStatus) {
        String query = "UPDATE " + this.databaseController.TABLES.record + " SET sync=? WHERE name=? AND path=?";
        try {
            PreparedStatement statement = this.databaseController.getConn().prepareStatement(query);
            statement.setInt(1, newStatus ? 1 : 0);
            statement.setString(2, fileName);
            statement.setString(3, filePath);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UPDATE_CHANGE_STATUS(String filePath, String fileName, boolean newStatus) {
        String query = "UPDATE " + this.databaseController.TABLES.record
                + " SET changeStatus=? WHERE name=? AND path=?";
        try {
            PreparedStatement statement = this.databaseController.getConn().prepareStatement(query);
            statement.setInt(1, newStatus ? 1 : 0);
            statement.setString(2, fileName);
            statement.setString(3, filePath);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UPDATE_DOWNLOAD_STATUS(String filePath, String fileName, boolean newStatus) {
        String query = "UPDATE " + this.databaseController.TABLES.record
                + " SET downloadStatus=? WHERE name=? AND path=?";
        try {
            FileRecord item = getByPathAndName(filePath, fileName);
            PreparedStatement statement = this.databaseController.getConn().prepareStatement(query);
            statement.setInt(1, newStatus ? 1 : 0);
            statement.setString(2, item.getName());
            statement.setString(3, item.getPath());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public FilePreview getSharedRecordPreview(String encryptedName) {
        ResultSetHandler<List<FilePreview>> rsh = new BeanListHandler<FilePreview>(FilePreview.class);
        try {
            List<FilePreview> records = this.databaseController.getLocalQueryRunner()
                    .query("SELECT * FROM " + this.databaseController.TABLES.sharedRecordTable
                            + " WHERE encryptedName='" + encryptedName + "'", rsh);
            if (records.size() != 0)
                return records.get(0);
            else
                return null;
        } catch (SQLException e) {
            return null;
        }
    }
}
