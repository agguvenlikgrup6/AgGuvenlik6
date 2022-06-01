package org.uludag.bmb.beans.database.query;

import org.uludag.bmb.beans.constants.Constants.TABLES;
import org.uludag.bmb.beans.database.sharing.SharedFile;
import org.uludag.bmb.factory.query.QueryFactory;

public class SharedFileQuery extends Query implements QueryFactory {

    public SharedFileQuery(String queryName) {
        super(queryName);
    }

    private final static String insert = "INSERT INTO " + TABLES.sharedFiles + "(recieverEmail, senderEmail, encryptedName, fileKeyPart1, fileKeyPart2) VALUES(?,?,?,?,?)";
    private final static String getByMailAndEncryptedName = "SELECT * FROM " + TABLES.sharedFiles + " WHERE recieverEmail=? AND encryptedName=?";
    private final static String deleteByMailAndEncryptedName = "DELETE FROM " + TABLES.sharedFiles + " WHERE recieverEmail=? AND encryptedName=?";
    @Override
    public String getQuery() {
        switch (queryName) {
            case "insert":
                return insert;
            case "getByEncryptedName":
                return getByMailAndEncryptedName;
            case "deleteByMailAndEncryptedName":
                return deleteByMailAndEncryptedName;
        }
        return null;
    }

    @Override
    public Class<?> getClassType() {
        return SharedFile.class;
    }

}
