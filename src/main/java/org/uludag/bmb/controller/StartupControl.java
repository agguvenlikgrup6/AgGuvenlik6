package org.uludag.bmb.controller;

import java.util.ArrayList;
import java.util.List;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.controller.database.DatabaseController;
import org.uludag.bmb.operations.dropbox.Client;
import org.uludag.bmb.operations.dropbox.FileOperations;

public class StartupControl {
    public void deletedFileControl() {
        List<FileRecord> cloud = StartupControl.GET_CLOUD_RECORDS();
        List<FileRecord> local = StartupControl.GET_LOCAL_RECORDS();

        for (int i = 0; i < local.size(); i++) {
            // if (local.get(i).getSync() != 0) {
                boolean check = false;
                for (FileRecord fileRecord : cloud) {
                    if (local.get(i).getEncryptedName().equals(fileRecord.getEncryptedName()) &&
                            local.get(i).getPath().equals(fileRecord.getPath())) {
                        check = true;
                    }
                }
                if (!check) {
                    FileOperations.DELETE_FILE(local.get(i).getPath(), local.get(i).getName());
                    DatabaseController dc = new DatabaseController();
                    dc.deleteRecord(local.get(i).getName(), local.get(i).getPath());
                    dc.insertNotification(local.get(i).getPath() + local.get(i).getName() + " buluttan silindiği için yerelden de silindi.");
                    // dc.insertNotification(local.get(i).getPath() + local.get(i).getName() + " buluttan silindiği için yerelden de senkronizasyona açık olduğu için silindi.");
                }
            // }
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

    public static final List<FileRecord> GET_LOCAL_RECORDS() {
        DatabaseController dc = new DatabaseController();
        List<FileRecord> fileRecords = dc.getAllRecords();
        ArrayList<FileRecord> f1 = new ArrayList<>();
        for (FileRecord f : fileRecords) {
            f1.add(new FileRecord(f.getName(), f.getPath(), f.getKey(), f.getModificationDate(), f.getHash(),
                    f.getEncryptedName(), f.getSync(), f.getChangeStatus()));
        }
        return f1;
    }

}
