package org.uludag.bmb.sync;

import java.io.File;
import java.io.IOException;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.uludag.bmb.beans.crypto.EncryptedFileData;
import org.uludag.bmb.beans.dropbox.DbClient;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.cryption.Crypto;

public class SyncAdaptor extends FileAlterationListenerAdaptor {

    @Override
    public void onFileChange(File file) {
        System.out.println("değişiklik");
    }

    @Override
    public void onFileCreate(File file) {
        System.out.println("oluşturma");
        int len = ConfigController.Settings.LoadSettings().getLocalDropboxPath().length();
        String cloudPath = file.getAbsolutePath().substring(len - 1,
                file.getAbsolutePath().length() - file.getName().length());

        DbClient dbClient = new DbClient(true);

        EncryptedFileData efd = Crypto.encryptFile(file);
        try {
            FileMetadata metaData = dbClient.getClient().files().uploadBuilder(cloudPath + efd.name).uploadAndFinish(efd.encryptedFile);
            // ConfigController.Crypto.Save(new EncryptedFileData(metaData, file.getName(),efd.key));
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("silme");
    }
}
