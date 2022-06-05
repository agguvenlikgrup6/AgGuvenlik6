package org.uludag.bmb.service.sync;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.sharing.SharedFileMetadata;

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
        createCacheDirectories();
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
        String syncPath = ConfigController.Settings.LoadSettings().getLocalDropboxPath();

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
                    notificationOperations
                            .insert(record.getPath() + record.getName() + " dosyasının senkronizasyonu kapatıldı");
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

    public void recievedFileControl() {
        try {
            List<SharedFileMetadata> entries = DropboxClient.sharing().listReceivedFiles().getEntries();
            for (SharedFileMetadata sharedFileMetadata : entries) {
                if (sharedFileMetadata.getName().contains("json")) {
                    String cacheFileAbsolutePath = Constants.ACCOUNT.cacheRecievedFileDirectory + sharedFileMetadata.getName();
                    // eğer dosya halihazırda yerelde kayıtlı değil ise indirir
                    String encryptedName = sharedFileMetadata.getName().split("\\+")[1].split("\\.")[0];
                    // dosya kayıt edilmiş mi kontrolü
                    RecievedFile recievedFile = recievedFileOperations.getByEncryptedName(encryptedName);
                    if (recievedFile == null) {
                        // paylaşılan json dosyası indirilir
                        // ./cache/recievedFiles/bmbgrup6@gmail.com+SAJkmsdfmdskJsajd.json isimli dosya olarak
                        DropboxClient.sharing().getSharedLinkFileBuilder(sharedFileMetadata.getPreviewUrl())
                                .download(new FileOutputStream(new File(cacheFileAbsolutePath)));
                        SharedFile sharedFile = ConfigController.SharedFileCredentials.Load(sharedFileMetadata.getName());
                        //dosya isminin şifresi ve anahtarı çözülür
                        RecievedFile newRecievedFile = Crypto.SHARE.recieveSharedFile(sharedFile);
                        recievedFileOperations.insert(newRecievedFile);

                        // kaydı tamamlanan dosyaya ihtiyaç olmadığı için silinir
                        Files.delete(Paths.get(cacheFileAbsolutePath));
                        // dosya kabul edildiği için dosya paylaşımından ayrılınır.
                        // paylaşılan dosyanın 2 sahibi olduğundan ve bu işlem sonrasında sahip sayısı
                        // 1'e düşeceğinden (yalnızca dosya sahibinin kendisi kalır) dosya sahibinin client'ı
                        // tarafından dosya otomatik olarak silinecektir (.json dosyası). Şifreli dosya
                        // kabul edildikten sonra ise şifreli dosyadan da çıkılacak ve silinecektir.
                        // DropboxClient.sharing().relinquishFileMembership(sharedFileMetadata.getId());
                        notificationOperations.insert(newRecievedFile.getDecryptedName() + " dosyası " + sharedFile.getSenderEmail() + " tarafından sizinle paylaşıldı!");
                    } else {
                        //do nothing, TBD for future use 
                        // System.out.println("paylaşılan yeni bir dosya yok");
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        // sharing klasörüne bakacak,
        // indirilmemiş olan dosyaları indirecek
        // json dosyalarındaki bilgiler recieved file tablosuna eklenecek

    }
}
