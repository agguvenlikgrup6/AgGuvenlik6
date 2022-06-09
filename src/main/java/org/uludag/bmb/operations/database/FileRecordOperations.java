package org.uludag.bmb.operations.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.uludag.bmb.beans.constants.Constants.TABLES;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.beans.dataproperty.CustomTableData;
import org.uludag.bmb.factory.query.QueryFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FileRecordOperations extends QueryExecutor {
    public void delete(String fileName, String filePath) {
        executeLocalQuery(QueryFactory.Records("delete"), filePath, fileName);
    }

    public FileRecord getByPathAndName(String filePath, String fileName) {
        List<FileRecord> records = executeLocalQuery(QueryFactory.Records("getByPathAndName"), filePath, fileName);
        if (records.size() != 0) {
            return records.get(0);
        } else {
            return null;
        }
    }

    public List<FileRecord> getAll() {
        List<FileRecord> records = executeLocalQuery(QueryFactory.Records("getAll"));
        return records;
    }

    public ObservableList<CustomTableData> getByPath(String filePath) {
        List<FileRecord> records = executeLocalQuery(QueryFactory.Records("getByPath"), filePath);
        ObservableList<CustomTableData> tableData = FXCollections.observableArrayList();

        for (FileRecord f : records) {
            tableData.add(new CustomTableData(f.getDownloadStatus(), f.getName(), f.getModificationDate(), f.getPath(),
                    f.getSync(), f.getChangeStatus(), f.getFileSize(),
                    Arrays.asList(f.getSharedAccounts().split(";")), f.getSharedAccounts()));
        }
        return tableData;
    }

    public void insert(FileRecord record) {
        executeLocalQuery(QueryFactory.Records("insert"), record.getName(), record.getPath(), record.getKey(),
                record.getModificationDate(), record.getHash(), record.getEncryptedName(), record.getSync(),
                record.getChangeStatus(),
                record.getDownloadStatus(), record.getFileSize(), record.getSharedAccounts(), record.getBusyStatus());
    }

    public FileRecord getbyPathAndEncryptedName(String filePath, String fileEncryptedName) {
        List<FileRecord> records = executeLocalQuery(QueryFactory.Records("getbyPathAndEncryptedName"), filePath,
                fileEncryptedName);
        if (records.size() != 0) {
            return records.get(0);
        } else {
            return null;
        }
    }

    public void updateSyncStatus(String filePath, String fileName, boolean newSyncStatus) {
        executeLocalQuery(QueryFactory.Records("updateSyncStatus"), newSyncStatus, filePath, fileName);
    }

    public void updateChangeStatus(String filePath, String fileName, boolean newChangeStatus) {
        executeLocalQuery(QueryFactory.Records("updateChangeStatus"), newChangeStatus, filePath, fileName);
    }

    public void updateDownloadStatus(String filePath, String fileName, boolean newDownloadStatus) {
        executeLocalQuery(QueryFactory.Records("updateDownloadStatus"), newDownloadStatus, filePath, fileName);
    }

    public void updateEncryptedName(String filePath, String newEncryptedName, String oldEncryptedName) {
        executeLocalQuery(QueryFactory.Records("updateEncryptedName"), newEncryptedName, oldEncryptedName, filePath);
    }

    public void updateKey(String filePath, String aesKey, String encryptedName) {
        executeLocalQuery(QueryFactory.Records("updateKey"), aesKey, encryptedName, filePath);
    }

    public void updateModificationDate(String filePath, String newlocalFileModificationDate, String encryptedName) {
        executeLocalQuery(QueryFactory.Records("updateModificationDate"), newlocalFileModificationDate, encryptedName,
                filePath);
    }

    public void updateFileSize(String filePath, String newFileSize, String encryptedName) {
        executeLocalQuery(QueryFactory.Records("updateFileSize"), newFileSize, encryptedName, filePath);
    }

    public void updateHash(String filePath, String newHash, String encryptedName) {
        executeLocalQuery(QueryFactory.Records("updateHash"), newHash, encryptedName, filePath);
    }

    public void updateSharedAccount(String userEmail, String filePath, String fileName){
        String query = "UPDATE " + TABLES.fileRecords
                + " SET sharedAccounts=sharedAccounts || ? WHERE path=? AND name=?";
        try {
            PreparedStatement statement = databaseController.getConn().prepareStatement(query);
            statement.setString(1, userEmail + ";");
            statement.setString(2, filePath);
            statement.setString(3, fileName);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cleanSharedAccounts(String newSharedAccounts, String filePath, String fileName) {
        executeLocalQuery(QueryFactory.Records("cleanSharedAccounts"), newSharedAccounts, filePath, fileName);
    }

    public void updateBusyStatus(int newBusyStatus, String filePath, String fileName) {
        executeLocalQuery(QueryFactory.Records("updateBusyStatus"), newBusyStatus, filePath, fileName);
    }
}
