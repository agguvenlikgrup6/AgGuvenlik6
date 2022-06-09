package org.uludag.bmb.operations.database;

import java.util.List;

import org.uludag.bmb.beans.database.sharing.RecievedFile;
import org.uludag.bmb.beans.dataproperty.CustomRecievedFileListView;
import org.uludag.bmb.factory.query.QueryFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RecievedFileOperations extends QueryExecutor {
    public RecievedFile getByEncryptedName(String encryptedName) {
        List<RecievedFile> recievedFiles = executeLocalQuery(QueryFactory.RecievedFile("getByEncryptedName"), encryptedName);
        if (recievedFiles.size() != 0) {
            return recievedFiles.get(0);
        } else {
            return null;
        }
    }

    public void insert(RecievedFile recievedFile) {
        executeLocalQuery(QueryFactory.RecievedFile("insert"), recievedFile.getSenderEmail(), recievedFile.getEncryptedName(), recievedFile.getDecryptedName(), recievedFile.getFileKey(), recievedFile.getModificationDate(), recievedFile.getHash(), recievedFile.getFileSize(),
                recievedFile.getPathHash());
    }

    public ObservableList<CustomRecievedFileListView> getAll() {
        List<RecievedFile> recievedFiles = executeLocalQuery(QueryFactory.RecievedFile("getAll"));
        ObservableList<CustomRecievedFileListView> tableData = FXCollections.observableArrayList();

        for (RecievedFile f : recievedFiles) {
            tableData.add(new CustomRecievedFileListView(f.getSenderEmail(), f.getDecryptedName(), f.getEncryptedName()));
        }

        return tableData;
    }

    public void deleteByEncryptedName(String encryptedName) {
        executeLocalQuery(QueryFactory.RecievedFile("deleteByEncryptedName"), encryptedName);
    }

    public void deleteByPathHash(String pathHash) {
        executeLocalQuery(QueryFactory.RecievedFile("deleteByPathHash"), pathHash);
    }

    public RecievedFile getByPathHash(String filePathHash) {
        List<RecievedFile> recievedFiles = executeLocalQuery(QueryFactory.RecievedFile("getByPathHash"), filePathHash);
        if (recievedFiles.size() != 0) {
            return recievedFiles.get(0);
        }else {
            return null;
        }
    }

    // public void deleteAll(){
    // executeCloudQuery(QueryFactory.RecievedFile(queryName), params)
    // }

}
