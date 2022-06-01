package org.uludag.bmb.operations.database;

import java.util.List;

import org.uludag.bmb.beans.database.sharing.RecievedFile;
import org.uludag.bmb.factory.query.QueryFactory;

public class RecievedFileOperations extends DatabaseOperations {
    public RecievedFile getByEncryptedName(String encryptedName) {
        List<RecievedFile> recievedFiles = executeLocalQuery(QueryFactory.RecievedFile("getByEncryptedName"), encryptedName);
        return recievedFiles.get(0);
    }
}
