package org.uludag.bmb.operations.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.uludag.bmb.controller.database.DatabaseController;

public class NotificationOperations {
    private DatabaseController databaseController;

    public NotificationOperations() {
        this.databaseController = new DatabaseController();
    }

    public List<String> getNotifications() {
        try {
            Statement statement = this.databaseController.getConn().createStatement();
            String query = "SELECT * FROM " + this.databaseController.TABLES.notification;
            ResultSet rst = statement.executeQuery(query);
            List<String> notifications = new ArrayList<String>();
            while (rst.next()) {
                notifications.add(rst.getString("message"));
            }
            query = "DELETE FROM " + this.databaseController.TABLES.notification;
            statement.execute(query);
            return notifications;
        } catch (SQLException ex) {
            return null;
        }
    }

    public void insertNotification(String notificationMessage) {
        String query = "INSERT INTO " + this.databaseController.TABLES.notification + " (message) values(?)";
        try {
            PreparedStatement statement = this.databaseController.getConn().prepareStatement(query);
            statement.setString(1, notificationMessage);

            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
