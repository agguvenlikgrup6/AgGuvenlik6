package org.uludag.bmb.service.sync;

import java.io.File;
import java.io.IOException;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.uludag.bmb.beans.authentication.DbClient;
import org.uludag.bmb.beans.crypto.EncryptedFileData;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.service.cryption.Crypto;

public class SyncAdaptor extends FileAlterationListenerAdaptor {

    @Override
    public void onFileChange(File file) {
        System.out.println("değişiklik");
    }

    @Override
    public void onFileCreate(File file) {
        System.out.println("oluşturma");
        if (SyncServer.getSyncStatus()) {
            int len = ConfigController.Settings.LoadSettings().getLocalDropboxPath().length();
            String cloudPath = file.getAbsolutePath().substring(len - 1,
                    file.getAbsolutePath().length() - file.getName().length());

            DbClient dbClient = new DbClient(true);

            EncryptedFileData efd = Crypto.encryptFile(file);
            try {
                FileMetadata metaData = dbClient.getClient().files().uploadBuilder(cloudPath + efd.name)
                        .uploadAndFinish(efd.encryptedFile);
                //database insert record
            } catch (DbxException | IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Sync servisi kapalı, yeniden başlatılıyor");
            SyncServer.stopSyncServer();
            Thread thread = new Thread(new SyncMonitor());
            thread.start();
            onFileCreate(file);
        }
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("silme");
    }
}
