package org.uludag.bmb.beans.query;

import org.uludag.bmb.beans.database.sharing.SentFile;
import org.uludag.bmb.factory.query.QueryFactory;

public class SharingQuery extends Query implements QueryFactory {
    public SharingQuery(String queryName) {
        super(queryName);
    }

    public final static String insertPublicKey = "";
    public final static String getSharedFileByEncryptedName = "";
    public final static String getApplicationUsersList = "";
    public final static String insertSharedFileKey = "";
    public final static String insertRecordPreview = "";

    @Override
    public String getQuery() {
        return null;
    }

    @Override
    public Class<?> getClassType() {
        return SentFile.class;
    }
}