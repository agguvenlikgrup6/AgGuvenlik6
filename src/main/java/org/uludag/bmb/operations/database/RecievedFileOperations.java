package org.uludag.bmb.operations.database;

import java.util.List;

import org.uludag.bmb.beans.database.sharing.RecievedFile;
import org.uludag.bmb.factory.query.QueryFactory;

public class RecievedFileOperations extends DatabaseOperations {
    public RecievedFile getByEncryptedName(String encryptedName) {
        List<RecievedFile> recievedFiles = executeLocalQuery(QueryFactory.RecievedFile("getByEncryptedName"),
                encryptedName);
        if (recievedFiles.size() != 0) {
            return recievedFiles.get(0);
        } else {
            return null;
        }
    }

    public void insert(RecievedFile recievedFile) {
        executeLocalQuery(QueryFactory.RecievedFile("insert"), recievedFile.getEncryptedName(),
                recievedFile.getDecryptedName(), recievedFile.getKey());
    }
}
