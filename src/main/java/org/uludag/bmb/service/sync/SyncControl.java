package org.uludag.bmb.service.sync;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.uludag.bmb.beans.constants.Constants;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.beans.database.sharing.RecievedFile;
import org.uludag.bmb.beans.database.sharing.SharedFile;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.operations.FileOperations;
import org.uludag.bmb.operations.database.FileRecordOperations;
import org.uludag.bmb.operations.database.NotificationOperations;
import org.uludag.bmb.operations.database.RecievedFileOperations;
import org.uludag.bmb.operations.dropbox.DropboxClient;
import org.uludag.bmb.service.cryption.Crypto;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.sharing.SharedFileMembers;
import com.dropbox.core.v2.sharing.SharedFileMetadata;

public class SyncControl {
    private final int START_DELAY = 5;
    private final int CYCLE_DELAY = 3;

    public SyncControl() {
        try {
            DropboxClient.client.files().createFolderV2("/sharing");
        } catch (DbxException e) {
        }
        downloadedFileControl();
        createCacheDirectories();
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                deletedFileControl();
                sentFileControl();
                recievedFileControl();
            }
        }, START_DELAY, CYCLE_DELAY, TimeUnit.SECONDS);
    }

    private static final NotificationOperations notificationOperations = Constants.notificationOperations;
    private static final FileRecordOperations fileRecordOperations = Constants.fileRecordOperations;
    private static final RecievedFileOperations recievedFileOperations = Constants.recievedFileOperations;

    public void deletedFileControl() {
        List<FileRecord> cloud = getCloudFiles();
        List<FileRecord> local = fileRecordOperations.getAll();

        for (int i = 0; i < local.size(); i++) {
            boolean check = false;
            for (FileRecord fileRecord : cloud) {
                if (local.get(i).getEncryptedName().equals(fileRecord.getEncryptedName()) && local.get(i).getPath().equals(fileRecord.getPath())) {
                    check = true;
                }
            }
            if (!check && local.get(i).getBusyStatus() == 0) {
                FileOperations.deleteFromLocal(local.get(i).getPath(), local.get(i).getName());
                fileRecordOperations.delete(local.get(i).getName(), local.get(i).getPath());
            }
        }

    }

    private List<FileRecord> getCloudFiles() {
        ArrayList<FileRecord> fileRecords = new ArrayList<FileRecord>();
        ListFolderResult result;
        try {
            result = DropboxClient.client.files().listFolderBuilder("").withIncludeDeleted(false).withRecursive(true).start();
            List<Metadata> entries = result.getEntries();

            for (Metadata entry : entries) {
                if (entry instanceof FileMetadata) {
                    String encryptedName = entry.getName();
                    String filePath = entry.getPathDisplay().substring(0, entry.getPathDisplay().length() - entry.getName().length());
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
        String syncPath = ConfigController.Settings.LoadSettings().getLocalDropboxPath();

        for (FileRecord record : records) {
            String fullPath = syncPath + record.getPath() + record.getName();
            File file = new File(fullPath);
            if (!file.exists()) {
                if (record.getDownloadStatus() == 1) {
                    notificationOperations.insert(record.getPath() + record.getName() + " dosyası uygulama kapalıyken silinmiş!");
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

    private void createCacheDirectories() {
        if (!Files.exists(Paths.get(Constants.ACCOUNT.cacheSharedFileDirectory))) {
            File fileFolder = new File(Constants.ACCOUNT.cacheSharedFileDirectory);
            fileFolder.mkdirs();
        }
        if (!Files.exists(Paths.get(Constants.ACCOUNT.cacheRecievedFileDirectory))) {
            File fileFolder = new File(Constants.ACCOUNT.cacheRecievedFileDirectory);
            fileFolder.mkdirs();
        }
    }

    public void sentFileControl() {
        try {
            List<Metadata> sharedJSONFiles = DropboxClient.files().listFolderBuilder("/sharing/").start().getEntries();
            for (Metadata jsonFileMD : sharedJSONFiles) {
                if (jsonFileMD instanceof FileMetadata) {
                    FileMetadata fileMetadata = (FileMetadata) jsonFileMD;
                    Date uploadDate = fileMetadata.getClientModified();
                    Date currentDate = new Date();
                    long difference = (currentDate.getTime() - uploadDate.getTime()) / 1000 % 60;
                    if (difference < 10.0) {
                        // do nothing
                    } else {
                        SharedFileMembers fileMembers = DropboxClient.sharing().listFileMembers(fileMetadata.getId());
                        if (fileMembers.getUsers().size() == 1) {
                            DropboxClient.files().deleteV2(fileMetadata.getPathDisplay());
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public void recievedFileControl() {
        try {
            List<SharedFileMetadata> entries = DropboxClient.sharing().listReceivedFiles().getEntries();
            for (SharedFileMetadata sharedFileMetadata : entries) {
                if (sharedFileMetadata.getName().contains("json")) {

                    String cacheFileAbsolutePath = Constants.ACCOUNT.cacheRecievedFileDirectory + sharedFileMetadata.getName();
                    String encryptedName = sharedFileMetadata.getName().split("\\+")[1].split("\\.")[0];

                    RecievedFile recievedFile = recievedFileOperations.getByEncryptedName(encryptedName);

                    FileOutputStream credentialsFile = new FileOutputStream(new File(cacheFileAbsolutePath));
                    DropboxClient.sharing().getSharedLinkFileBuilder(sharedFileMetadata.getPreviewUrl()).download(credentialsFile);

                    SharedFile sharedFile = ConfigController.SharedFileCredentials.Load(sharedFileMetadata.getName());
                    credentialsFile.close();

                    RecievedFile newRecievedFile = Crypto.SHARE.recieveSharedFile(sharedFile);
                    if (recievedFile == null) {
                        recievedFileOperations.insert(newRecievedFile);
                        notificationOperations.insert(newRecievedFile.getDecryptedName() + " dosyası " + sharedFile.getSenderEmail() + " tarafından sizinle paylaşıldı!");
                    } else {
                        if (encryptedName.equals(recievedFile.getEncryptedName())) {
                            return;
                        } else {
                            recievedFileOperations.deleteByPathHash(newRecievedFile.getPathHash());
                            recievedFileOperations.insert(newRecievedFile);
                        }
                    }
                    Files.delete(Paths.get(cacheFileAbsolutePath));
                }
            }
        } catch (Exception e) {
        }
    }
}