package org.uludag.bmb.operations.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.uludag.bmb.beans.constants.Constants.TABLES;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.beans.dataproperty.CustomTableView;
import org.uludag.bmb.factory.query.QueryFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FileRecordOperations extends DatabaseOperations {
    public void delete(String fileName, String filePath) {
        executeLocalQuery(QueryFactory.Records("delete"), filePath, fileName);
    }

    public FileRecord getByPathAndName(String filePath, String fileName) {
        List<FileRecord> records = executeLocalQuery(QueryFactory.Records("getByPathAndName"), filePath, fileName);
        if(records.size() != 0){
            return records.get(0);
        }else {
            return null;
        }
    }

    public List<FileRecord> getAll() {
        List<FileRecord> records = executeLocalQuery(QueryFactory.Records("getAll"));
        return records;
    }

    public ObservableList<CustomTableView> getByPath(String filePath) {
        List<FileRecord> records = executeLocalQuery(QueryFactory.Records("getByPath"), filePath);
        ObservableList<CustomTableView> tableData = FXCollections.observableArrayList();

        for (FileRecord f : records) {
            tableData.add(new CustomTableView(f.getDownloadStatus(), f.getName(), f.getModificationDate(), f.getPath(),
                    f.getSync(), f.getChangeStatus(), f.getFileSize(),
                    Arrays.asList(f.getSharedAccounts().split(";"))));
        }
        return tableData;
    }

    public void insertRecord(FileRecord record) {
        executeLocalQuery(QueryFactory.Records("insert"), record.getName(), record.getPath(), record.getKey(),
                record.getModificationDate(), record.getHash(), record.getEncryptedName(), record.getSync(),
                record.getChangeStatus(),
                record.getDownloadStatus(), record.getFileSize(), record.getSharedAccounts());
    }

    public FileRecord getbyPathAndEncryptedName(String filePath, String fileEncryptedName) {
        List<FileRecord> records = executeLocalQuery(QueryFactory.Records("getbyPathAndEncryptedName"), filePath,
                fileEncryptedName);
        if(records.size() != 0){
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

    public void updateSharedAccounts(List<String> userEmailList, String filePath, String fileName) {
        String query = "UPDATE " + TABLES.fileRecords
                + " SET sharedAccounts=sharedAccounts || ? WHERE path=? AND name=?";
        try {
            PreparedStatement statement = databaseController.getConn().prepareStatement(query);
            statement.setString(1, String.join(";", userEmailList) + ";");
            statement.setString(2, filePath);
            statement.setString(3, fileName);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
