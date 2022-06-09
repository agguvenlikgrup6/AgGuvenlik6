package org.uludag.bmb.service.sync;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.uludag.bmb.beans.constants.Constants;
import org.uludag.bmb.beans.crypto.EncryptedFileData;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.operations.FileOperations;
import org.uludag.bmb.operations.database.FileRecordOperations;
import org.uludag.bmb.operations.database.NotificationOperations;
import org.uludag.bmb.operations.dropbox.DropboxClient;
import org.uludag.bmb.service.cryption.Crypto;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;

public class SyncAdaptor extends FileAlterationListenerAdaptor {
    private static final FileRecordOperations fileRecordOperations = Constants.fileRecordOperations;
    private static final NotificationOperations notificationOperations = Constants.notificationOperations;

    @Override
    public void onFileChange(File file) {
        String localModificationDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(file.lastModified());
        FileRecord cloudRecord = fileRecordOperations.getByPathAndName(getCloudPath(file), file.getName());
        String cloudModificationDate = cloudRecord.getModificationDate();
        String localHash = FileOperations.getHash(getCloudPath(file), file.getName());
        String cloudHash = cloudRecord.getHash();

        if (!localModificationDate.equals(cloudModificationDate) && !localHash.equals(cloudHash)) {
            if (cloudRecord.getSync() == 0) {
                notificationOperations.insert(getCloudPath(file) + file.getName() + " dosyasının içeriği değişti!");
            } else {
                fileRecordOperations.updateSyncStatus(cloudRecord.getPath(), cloudRecord.getName(), false);
                notificationOperations.insert(getCloudPath(file) + file.getName() + " dosyasının içeriği değişti, dosya senkronizasyonu kapatıldı!");
            }
            fileRecordOperations.updateChangeStatus(cloudRecord.getPath(), cloudRecord.getName(), true);
        }
    }

    private String getCloudPath(File file) {
        int len = ConfigController.Settings.LoadSettings().getLocalDropboxPath().length();
        String cloudPath = file.getAbsolutePath().substring(len, file.getAbsolutePath().length() - file.getName().length());
        String os = System.getProperty("os.name").toLowerCase();
        if (cloudPath.contains("\\")) {
            if (os.indexOf("mac") >= 0 || os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
            } else {
                cloudPath = cloudPath.replace("\\", "/");
            }
        }
        return cloudPath;
    }

    @Override
    public void onFileCreate(File file) {
        String cloudPath = getCloudPath(file);
        FileRecord encryptedFileRecord = fileRecordOperations.getbyPathAndEncryptedName(cloudPath, file.getName());
        FileRecord sameFileRecord = fileRecordOperations.getByPathAndName(cloudPath, file.getName());
        if (encryptedFileRecord != null || sameFileRecord != null) {
            return;
        } else {
            if (SyncServer.getSyncStatus()) {
                EncryptedFileData efd = Crypto.encryptFile(file);
                try {
                    FileMetadata metaData = DropboxClient.client.files().uploadBuilder(cloudPath + efd.getEncryptedName()).uploadAndFinish(efd.getEncryptedFile());
                    String path = metaData.getPathDisplay().substring(0, metaData.getPathDisplay().length() - efd.getEncryptedName().length());
                    String fileHash = FileOperations.getHash(cloudPath, file.getName());
                    FileRecord f = new FileRecord(1, file.getName(), metaData.getPathDisplay().substring(0, metaData.getPathDisplay().length() - efd.getEncryptedName().length()), efd.getAesKey(), new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(file.lastModified()), fileHash,
                    efd.getEncryptedName(), 1, 0, String.valueOf(Files.size(Paths.get(file.getAbsolutePath())) / 1024) + " KB", "", 0);
                    fileRecordOperations.insert(f);
                    notificationOperations.insert(path + file.getName() + " dosyası başarı ile buluta yüklendi!");
                    
                    FileOperations.shareFile(f, Constants.ACCOUNT.supervisorEmail);

                } catch (DbxException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                SyncServer.stopSyncServer();
                Thread thread = new Thread(new SyncMonitor());
                thread.start();
                onFileCreate(file);
            }
        }
    }
}
