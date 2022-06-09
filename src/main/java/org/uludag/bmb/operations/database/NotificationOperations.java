package org.uludag.bmb.operations.database;

import java.util.List;

import org.uludag.bmb.beans.database.Notification;
import org.uludag.bmb.factory.query.QueryFactory;

public class NotificationOperations extends QueryExecutor{
    public List<Notification> getAll() {
        List<Notification> notifications = executeLocalQuery(QueryFactory.Notification("getAll"));
        return notifications;
    }

    public void insert(String notificationMessage) {
        executeLocalQuery(QueryFactory.Notification("insert"), notificationMessage);
    }

    public void deleteAll() {
        executeLocalQuery(QueryFactory.Notification("deleteAll"));
    }
}
