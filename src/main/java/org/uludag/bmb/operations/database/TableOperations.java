package org.uludag.bmb.operations.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.uludag.bmb.controller.database.DatabaseController;

public class TableOperations {
    private DatabaseController databaseController;

    public TableOperations() {
        this.databaseController = new DatabaseController();
    }

    public void createRecordTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + this.databaseController.TABLES.record +
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
            PreparedStatement statement = this.databaseController.getConn().prepareStatement(query);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createNotificationTable() {
        String query = "CREATE TABLE IF NOT EXISTS " +
                this.databaseController.TABLES.notification +
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

    public void createPrivateKeyTable() {
        String query = "CREATE TABLE IF NOT EXISTS " +
                this.databaseController.TABLES.privateKey +
                "(" +
                "privateKey TEXT NOT NULL" +
                ")";
        try {
            PreparedStatement statement = this.databaseController.getConn().prepareStatement(query);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
