package org.uludag.bmb.operations.database;

import org.uludag.bmb.controller.database.DatabaseController;

public class DatabaseOperations {
    protected DatabaseController databaseController;

    public DatabaseOperations(){
        this.databaseController = new DatabaseController();
    }
}
