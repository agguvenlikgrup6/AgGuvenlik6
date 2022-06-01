package org.uludag.bmb.factory.query;

import org.uludag.bmb.beans.query.NotificationQuery;
import org.uludag.bmb.beans.query.RecievedQuery;
import org.uludag.bmb.beans.query.RecordsQuery;
import org.uludag.bmb.beans.query.SharingQuery;

public interface QueryFactory {
    public String getQuery();
    public Class<?> getClassType();
    
    public static QueryFactory Records(String queryName) {
        return new RecordsQuery(queryName);
    }

    public static QueryFactory Notification(String queryName) {
        return new NotificationQuery(queryName);
    }

    public static QueryFactory Sharing(String queryName) {
        return new SharingQuery(queryName);
    }

    public static QueryFactory RecievedFile(String queryName) {
        return new RecievedQuery(queryName);
    }
}
