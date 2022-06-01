package org.uludag.bmb.factory.query;

import org.uludag.bmb.beans.database.query.NotificationQuery;
import org.uludag.bmb.beans.database.query.RecievedQuery;
import org.uludag.bmb.beans.database.query.SharedFileQuery;
import org.uludag.bmb.beans.database.query.UserInformationQuery;
import org.uludag.bmb.beans.database.query.FileRecordQuery;

public interface QueryFactory {
    public String getQuery();
    public Class<?> getClassType();
    
    public static QueryFactory Records(String queryName) {
        return new FileRecordQuery(queryName);
    }

    public static QueryFactory Notification(String queryName) {
        return new NotificationQuery(queryName);
    }

    public static QueryFactory RecievedFile(String queryName) {
        return new RecievedQuery(queryName);
    }

    public static QueryFactory SharedFile(String queryName){
        return new SharedFileQuery(queryName);
    }

    public static QueryFactory UserInformation(String queryName){
        return new UserInformationQuery(queryName);
    }
}
