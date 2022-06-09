package org.uludag.bmb.beans.database.query;

import org.uludag.bmb.beans.constants.Constants.TABLES;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.factory.query.QueryFactory;

public class FileRecordQuery extends Query implements QueryFactory {
    public FileRecordQuery(String queryName) {
        super(queryName);
    }

    private final static String delete = "DELETE FROM " + TABLES.fileRecords + " WHERE path=? AND name=?";
    private final static String getByPathAndName = "SELECT * FROM " + TABLES.fileRecords + " WHERE path=? AND name=?";
    private final static String getByEncryptedName = "SELECT * FROM " + TABLES.fileRecords + " WHERE path=? AND name=?";
    private final static String getAll = "SELECT * FROM " + TABLES.fileRecords;
    private final static String getByPath = "SELECT * FROM " + TABLES.fileRecords + " WHERE path=?";
    private final static String insert = "INSERT INTO " + TABLES.fileRecords + " (name, path, key, modificationDate, hash, encryptedName, sync, changeStatus, downloadStatus, fileSize, sharedAccounts, busyStatus)" + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String getbyPathAndEncryptedName = "SELECT * FROM " + TABLES.fileRecords + " WHERE path=? AND encryptedName=?";
    private final static String updateSyncStatus = "UPDATE " + TABLES.fileRecords + " SET sync=? WHERE path=? AND name=?";;
    private final static String updateChangeStatus = "UPDATE " + TABLES.fileRecords + " SET changeStatus=? WHERE path=? AND name=?";
    private final static String updateDownloadStatus = "UPDATE " + TABLES.fileRecords + " SET downloadStatus=? WHERE path=? AND name=?";
    private final static String updateRecordSharedAccounts = "UPDATE " + TABLES.fileRecords + " SET sharedAccounts=sharedAccounts || ? WHERE path=? AND name=?";

    private final static String updateEncryptedName = "UPDATE " + TABLES.fileRecords + " SET encryptedName=? WHERE encryptedName=? AND path=?";
    private final static String updateKey = "UPDATE " + TABLES.fileRecords + " SET key=? WHERE encryptedName=? AND path=?";
    private final static String updateModificationDate = "UPDATE " + TABLES.fileRecords + " SET modificationDate=? WHERE encryptedName=? AND path=?";
    private final static String updateFileSize = "UPDATE " + TABLES.fileRecords + " SET fileSize=? WHERE encryptedName=? AND path=?";
    private final static String updateHash = "UPDATE " + TABLES.fileRecords + " SET hash=? WHERE encryptedName=? AND path=?";
    private final static String cleanSharedAccounts = "UPDATE " + TABLES.fileRecords + " SET sharedAccounts=? WHERE path=? AND name=?";
    private final static String updateBusyStatus = "UPDATE " + TABLES.fileRecords + " SET busyStatus=? WHERE path=? AND name=?";

    @Override
    public String getQuery() {
        switch (queryName) {
        case "delete":
            return delete;
        case "getByPathAndName":
            return getByPathAndName;
        case "getByEncryptedName":
            return getByEncryptedName;
        case "getAll":
            return getAll;
        case "getByPath":
            return getByPath;
        case "insert":
            return insert;
        case "getbyPathAndEncryptedName":
            return getbyPathAndEncryptedName;
        case "updateSyncStatus":
            return updateSyncStatus;
        case "updateChangeStatus":
            return updateChangeStatus;
        case "updateDownloadStatus":
            return updateDownloadStatus;
        case "updateRecordSharedAccounts":
            return updateRecordSharedAccounts;
        case "updateEncryptedName":
            return updateEncryptedName;
        case "updateKey":
            return updateKey;
        case "updateModificationDate":
            return updateModificationDate;
        case "updateFileSize":
            return updateFileSize;
        case "updateHash":
            return updateHash;
        case "cleanSharedAccounts":
            return cleanSharedAccounts;
        case "updateBusyStatus":
            return updateBusyStatus;
        }
        return null;
    }

    @Override
    public Class<?> getClassType() {
        return FileRecord.class;
    }
}