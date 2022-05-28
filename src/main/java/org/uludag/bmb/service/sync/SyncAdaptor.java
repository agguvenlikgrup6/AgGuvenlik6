package org.uludag.bmb.service.sync;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.uludag.bmb.beans.crypto.EncryptedFileData;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.operations.database.FileRecordOperations;
import org.uludag.bmb.operations.database.NotificationOperations;
import org.uludag.bmb.operations.dropbox.Client;
import org.uludag.bmb.operations.dropbox.FileOperations;
import org.uludag.bmb.service.cryption.Crypto;

public class SyncAdaptor extends FileAlterationListenerAdaptor {
    private static final FileRecordOperations fileRecordOperations = new FileRecordOperations();
    private static final NotificationOperations notificationOperations = new NotificationOperations();

    @Override
    public void onFileChange(File file) {
        String localModificationDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(file.lastModified());
        FileRecord cloudRecord = fileRecordOperations.getByPathAndName(getCloudPath(file), file.getName());
        String cloudModificationDate = cloudRecord.getModificationDate();
        String localHash = FileOperations.GET_HASH(getCloudPath(file), file.getName());
        String cloudHash = cloudRecord.getHash();

        if (cloudRecord.getSync() == 1) {
            if (!localModificationDate.equals(cloudModificationDate) && !localHash.equals(cloudHash)) {
                fileRecordOperations.UPDATE_SYNC_STATUS(cloudRecord.getPath(), cloudRecord.getName(), false);
                fileRecordOperations.UPDATE_CHANGE_STATUS(cloudRecord.getPath(), cloudRecord.getName(), true);
                notificationOperations.insertNotification(getCloudPath(file) + file.getName()
                        + " dosyasında değişiklik oldu. Dosya senkronizasyona kapatıldı!");

            }
        }
    }

    private String getCloudPath(File file) {
        int len = ConfigController.Settings.LoadSettings().getLocalDropboxPath().length();
        String cloudPath = file.getAbsolutePath().substring(len,
                file.getAbsolutePath().length() - file.getName().length());
        return cloudPath;
    }

    @Override
    public void onFileCreate(File file) {
        String cloudPath = getCloudPath(file);
        FileRecord encryptedFileRecord = fileRecordOperations.getByPathAndEncryptedName(cloudPath, file.getName());
        FileRecord sameFileRecord = fileRecordOperations.getByPathAndName(cloudPath, file.getName());
        if (encryptedFileRecord != null || sameFileRecord != null) {
            return;
        } else {
            if (SyncServer.getSyncStatus()) {
                EncryptedFileData efd = Crypto.encryptFile(file);
                try {
                    FileMetadata metaData = Client.client.files().uploadBuilder(cloudPath + efd.name)
                            .uploadAndFinish(efd.encryptedFile);
                    String path = metaData.getPathDisplay().substring(0,
                            metaData.getPathDisplay().length() - efd.name.length());
                    String fileHash = FileOperations.GET_HASH(cloudPath, file.getName());
                    fileRecordOperations.INSERT(new FileRecord(1, file.getName(),
                            metaData.getPathDisplay().substring(0,
                                    metaData.getPathDisplay().length() - efd.name.length()),
                            efd.key,
                            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(file.lastModified()),
                            fileHash,
                            efd.name, 1, 0));
                    notificationOperations.insertNotification(path + file.getName() + " dosyası başarı ile yüklendi!");

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
    }
}
