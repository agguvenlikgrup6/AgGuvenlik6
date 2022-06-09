package org.uludag.bmb.operations.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.uludag.bmb.beans.constants.Constants.TABLES;

public class TableOperations extends QueryExecutor {

    public void createLocalTables() {
        createNotificationTable();
        createRecordTable();
        createRecievedFilesTable();
    }

    private void createRecordTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLES.fileRecords +
                "(" +
                "id integer PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "path TEXT NOT NULL," +
                "key TEXT NOT NULL," +
                "modificationDate TEXT NOT NULL," +
                "hash TEXT NOT NULL," +
                "encryptedName TEXT NOT NULL," +
                "sync BOOLEAN NOT NULL CHECK(sync IN(0, 1))," +
                "downloadStatus BOOLEAN NOT NULL CHECK(downloadStatus IN(0,1))," +
                "changeStatus BOOLEAN NOT NULL CHECK(changeStatus IN(0,1))," +
                "fileSize TEXT NOT NULL," +
                "sharedAccounts TEXT NOT NULL" +
                ")";
        try {
            PreparedStatement statement = this.databaseController.getConn().prepareStatement(query);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createRecievedFilesTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLES.recievedFiles +
                "(" +
                "id integer PRIMARY KEY," +
                "senderEmail TEXT NOT NULL," +
                "encryptedName TEXT NOT NULL," +
                "decryptedName TEXT NOT NULL," +
                "fileKey TEXT NOT NULL," +
                "modificationDate TEXT NOT NULL," +
                "hash TEXT NOT NULL, " + 
                "fileSize TEXT NOT NULL," +
                "pathHash TEXT NOT NULL" + 
                ")";
        try {
            PreparedStatement statement = this.databaseController.getConn().prepareStatement(query);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createNotificationTable() {
        String query = "CREATE TABLE IF NOT EXISTS " +
                TABLES.notification +
                "(" +
                "id integer PRIMARY KEY," +
                "message TEXT NOT NULL" +
                ")";
        try {
            PreparedStatement statement = this.databaseController.getConn().prepareStatement(query);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
