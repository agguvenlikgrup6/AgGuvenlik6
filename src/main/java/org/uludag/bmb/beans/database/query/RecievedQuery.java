package org.uludag.bmb.beans.database.query;

import org.uludag.bmb.beans.constants.Constants.TABLES;
import org.uludag.bmb.beans.database.sharing.RecievedFile;
import org.uludag.bmb.factory.query.QueryFactory;

public class RecievedQuery extends Query implements QueryFactory {
    public RecievedQuery(String queryName) {
        super(queryName);
    }

    private final static String getByEncryptedName = "SELECT * FROM " + TABLES.recievedFiles + " WHERE encryptedName=?";
    private final static String insert = "INSERT INTO " + TABLES.recievedFiles
            + " (senderEmail, encryptedName, decryptedName, fileKey) VALUES(?,?,?,?)";
    private final static String getAll = "SELECT * FROM " + TABLES.recievedFiles;

    @Override
    public String getQuery() {
        switch (queryName) {
            case "getByEncryptedName":
                return getByEncryptedName;
            case "insert":
                return insert;
            case "getAll":
                return getAll;
        }
        return null;
    }

    @Override
    public Class<?> getClassType() {
        return RecievedFile.class;
    }
}
