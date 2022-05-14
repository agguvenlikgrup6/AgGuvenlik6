package org.uludag.bmb.service.sync;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.uludag.bmb.beans.crypto.EncryptedFileData;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.controller.database.DatabaseController;
import org.uludag.bmb.operations.dropbox.Client;
import org.uludag.bmb.service.cryption.Crypto;

public class SyncAdaptor extends FileAlterationListenerAdaptor {

    @Override
    public void onFileChange(File file) {
        System.out.println("değişiklik");
    }

    @Override
    public void onFileCreate(File file) {
        if (SyncServer.getSyncStatus()) {
            int len = ConfigController.Settings.LoadSettings().getLocalDropboxPath().length();
            String cloudPath = file.getAbsolutePath().substring(len,
                    file.getAbsolutePath().length() - file.getName().length());

            EncryptedFileData efd = Crypto.encryptFile(file);
            try {
                FileMetadata metaData = Client.client.files().uploadBuilder(cloudPath + efd.name)
                        .uploadAndFinish(efd.encryptedFile);
                DatabaseController dc = new DatabaseController();
                String path = metaData.getPathDisplay().substring(0,
                        metaData.getPathDisplay().length() - efd.name.length());
                dc.insertRecord(new FileRecord(file.getName(),
                        metaData.getPathDisplay().substring(0,
                                metaData.getPathDisplay().length() - efd.name.length()),
                        efd.key,
                        new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(metaData.getServerModified()), "eklenmedi",
                        efd.name, 0));
                dc.insertNotification(path + ", dizinine " + file.getName() + " dosyası başarı ile yüklendi!");
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
