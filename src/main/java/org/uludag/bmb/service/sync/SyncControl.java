package org.uludag.bmb.service.sync;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import org.uludag.bmb.beans.constants.Constants;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.controller.localconfig.LocalConfigController;
import org.uludag.bmb.operations.FileOperations;
import org.uludag.bmb.operations.database.FileRecordOperations;
import org.uludag.bmb.operations.database.NotificationOperations;
import org.uludag.bmb.operations.dropbox.DropboxClient;

public class SyncControl {
    private final int START_DELAY = 5;
    private final int CYCLE_DELAY = 3;

    public SyncControl() {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                deletedFileControl();
                recievedFileControl();
            }
        }, START_DELAY, CYCLE_DELAY, TimeUnit.SECONDS);
        downloadedFileControl();
    }

    private static final NotificationOperations notificationOperations = Constants.notificationOperations;
    private static final FileRecordOperations fileRecordOperations = Constants.fileRecordOperations;

    public void deletedFileControl() {
        List<FileRecord> cloud = getCloudFiles();
        List<FileRecord> local = fileRecordOperations.getAll();

        for (int i = 0; i < local.size(); i++) {
            boolean check = false;
            for (FileRecord fileRecord : cloud) {
                if (local.get(i).getEncryptedName().equals(fileRecord.getEncryptedName()) &&
                        local.get(i).getPath().equals(fileRecord.getPath())) {
                    check = true;
                }
            }
            if (!check) {
                FileOperations.deleteFromLocal(local.get(i).getPath(), local.get(i).getName());
                fileRecordOperations.delete(local.get(i).getName(), local.get(i).getPath());
            }
        }

    }

    private List<FileRecord> getCloudFiles() {
        ArrayList<FileRecord> fileRecords = new ArrayList<FileRecord>();
        ListFolderResult result;
        try {
            result = DropboxClient.client.files().listFolderBuilder("").withIncludeDeleted(false).withRecursive(true)
                    .start();
            List<Metadata> entries = result.getEntries();

            for (Metadata entry : entries) {
                if (entry instanceof FileMetadata) {
                    String encryptedName = entry.getName();
                    String filePath = entry.getPathDisplay().substring(0,
                            entry.getPathDisplay().length() - entry.getName().length());
                    fileRecords.add(new FileRecord(encryptedName, filePath));
                }
            }
            return fileRecords;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void downloadedFileControl() {
        List<FileRecord> records = fileRecordOperations.getAll();
        String syncPath = LocalConfigController.Settings.LoadSettings().getLocalDropboxPath();

        for (FileRecord record : records) {
            String fullPath = syncPath + record.getPath() + record.getName();
            File file = new File(fullPath);
            if (!file.exists()) {
                if (record.getDownloadStatus() == 1) {
                    notificationOperations.insert(
                            record.getPath() + record.getName() + " dosyası uygulama kapalıyken silinmiş!");
                    fileRecordOperations.updateDownloadStatus(record.getPath(), record.getName(), false);
                }
                if (record.getSync() == 1) {
                    notificationOperations.insert(record.getPath() + record.getName() + " dosyasının senkronizasyonu kapatıldı");
                    fileRecordOperations.updateSyncStatus(record.getPath(), record.getName(), false);
                }
            } else {
                fileRecordOperations.updateDownloadStatus(record.getPath(), record.getName(), true);
            }
        }
    }

    public void recievedFileControl() {
        
    }
}
