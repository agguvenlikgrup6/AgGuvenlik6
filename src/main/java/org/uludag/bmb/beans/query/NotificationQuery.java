package org.uludag.bmb.beans.query;

import org.uludag.bmb.beans.constants.Constants.TABLES;
import org.uludag.bmb.beans.database.Notification;
import org.uludag.bmb.factory.query.QueryFactory;

public class NotificationQuery extends Query implements QueryFactory {
    public NotificationQuery(String queryName) {
        super(queryName);
    }

    public final static String getAll = "SELECT * FROM " + TABLES.notification;
    public final static String insert = "INSERT INTO " + TABLES.notification + " (message) VALUES(?)";
    public final static String deleteAll = "DELETE FROM " + TABLES.notification;

    @Override
    public String getQuery() {
        switch (queryName) {
            case "getAll":
                return getAll;
            case "insert": 
                return insert;
            case "deleteAll":
                return deleteAll;
        }
        return null;
    }

    @Override
    public Class<?> getClassType() {
        return Notification.class;
    }
}