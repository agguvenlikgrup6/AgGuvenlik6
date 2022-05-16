package org.uludag.bmb.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dropbox.core.http.SSLConfig.LoadException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.controller.database.DatabaseController;
import org.uludag.bmb.operations.dropbox.Client;

public class StartupControl {
    public static void main(String[] args) {
        StartupControl sc = new StartupControl();
        sc.deletedFileControl();

    }

    public void deletedFileControl() {
        List<FileRecord> cloudRecords = GET_CLOUD_RECORDS();
        List<FileRecord> localRecords = GET_LOCAL_RECORDS();

        for (FileRecord cloudRecord : cloudRecords) {
            boolean flag = false;
            for (FileRecord localRecord : localRecords) {
                if (cloudRecord.getEncryptedName() == localRecord.getEncryptedName()
                        && cloudRecord.getPath() == localRecord.getPath()) {
                    flag = true;
                }
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

    public static final List<FileRecord> GET_LOCAL_RECORDS() {
        DatabaseController dc = new DatabaseController();
        List<FileRecord> f = dc.getAllRecords();
        ArrayList<FileRecord> fileRecords = new ArrayList<>();
        for (FileRecord fileRecord : f) {
            fileRecords.add(new FileRecord(fileRecord.getEncryptedName(), fileRecord.getPath()));
        }
        return fileRecords;
    }

}
