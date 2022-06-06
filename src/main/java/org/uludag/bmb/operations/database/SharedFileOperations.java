package org.uludag.bmb.operations.database;

import java.util.List;

import org.uludag.bmb.beans.database.sharing.SharedFile;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.factory.query.QueryFactory;

public class SharedFileOperations extends DatabaseOperations {
    public void insert(SharedFile sharedFile) {
        executeCloudQuery(QueryFactory.SharedFile("insert"), sharedFile.getRecieverEmail(), sharedFile.getSenderEmail(),
                sharedFile.getEncryptedName(), sharedFile.getFileKeyPart1(), sharedFile.getFileKeyPart2(),
                sharedFile.getFileKeyPart3(), sharedFile.getFileKeyPart4(),
                sharedFile.getModificationDate(), sharedFile.getHash(), sharedFile.getFileSize());
    }

    public SharedFile getByMailAndEncryptedName(String encryptedName) {
        String email = ConfigController.Settings.LoadSettings().getUserEmail();
        List<SharedFile> sharedFiles = executeCloudQuery(QueryFactory.SharedFile("getByEncryptedName"), email,
                encryptedName);
        if (sharedFiles.size() != 0) {
            return sharedFiles.get(0);
        } else {
            return null;
        }
    }

    public void deleteByMailAndEncryptedName(String encryptedName) {
        String email = ConfigController.Settings.LoadSettings().getUserEmail();
        executeCloudQuery(QueryFactory.SharedFile("deleteByMailAndEncryptedName"), email, encryptedName);
    }
}
