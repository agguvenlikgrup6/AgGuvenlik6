package org.uludag.bmb.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import org.uludag.bmb.beans.constants.Constants;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.operations.database.FileRecordOperations;
import org.uludag.bmb.operations.database.NotificationOperations;
import org.uludag.bmb.operations.dropbox.Client;
import org.uludag.bmb.operations.dropbox.FileOperations;

public class StartupControl {
    public StartupControl(){
        deletedFileControl();
        downloadFileControl();
    }
    private static final NotificationOperations notificationOperations = Constants.notificationOperations;
    private static final FileRecordOperations fileRecordOperations = Constants.fileRecordOperations;

    public void deletedFileControl() {
        List<FileRecord> cloud = GET_CLOUD_RECORDS();
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
                FileOperations.deleteFile(local.get(i).getPath(), local.get(i).getName());
                fileRecordOperations.delete(local.get(i).getName(), local.get(i).getPath());
                notificationOperations.insert(local.get(i).getPath() + local.get(i).getName()
                        + " buluttan silindiği için yerelden de silindi.");
            }
        }

    }

    public static final List<FileRecord> GET_CLOUD_RECORDS() {
        ArrayList<FileRecord> fileRecords = new ArrayList<FileRecord>();
        ListFolderResult result;
        try {
            result = Client.client.files().listFolderBuilder("").withIncludeDeleted(false).withRecursive(true).start();
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

    public void downloadFileControl() {
        List<FileRecord> records = fileRecordOperations.getAll();
        String syncPath = ConfigController.Settings.LoadSettings().getLocalDropboxPath();

        for (FileRecord record : records) {
            String fullPath = syncPath + record.getPath() + record.getName();
            File file = new File(fullPath);
            if (!file.exists()) {
                fileRecordOperations.updateDownloadStatus(record.getPath(), record.getName(), false);
            } else {
                fileRecordOperations.updateDownloadStatus(record.getPath(), record.getName(), true);
            }

        }
    }

}
