package org.uludag.bmb.operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.uludag.bmb.beans.constants.Constants;
import org.uludag.bmb.beans.constants.Constants.ACCOUNT;
import org.uludag.bmb.beans.crypto.EncryptedFileData;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.beans.database.sharing.SharedFile;
import org.uludag.bmb.beans.dataproperty.CustomTableView;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.operations.database.FileRecordOperations;
import org.uludag.bmb.operations.database.NotificationOperations;
import org.uludag.bmb.operations.dropbox.DropboxClient;
import org.uludag.bmb.service.cryption.Crypto;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.MemberSelector;

import javafx.collections.ObservableList;

public class FileOperations {
    private static final String localSyncPath = ACCOUNT.localSyncPath;
    private static final FileRecordOperations FILE_RECORD_OPERATIONS = Constants.fileRecordOperations;
    private static final NotificationOperations NOTIFICATION_OPERATIONS = Constants.notificationOperations;

    public static final void downloadFile(String filePath, String fileName) {
        new Thread(() -> {
            try {
                FileRecord fileRecord = FILE_RECORD_OPERATIONS.getByPathAndName(filePath, fileName);

                String fileDirectory = localSyncPath + filePath;
                String absoluteFilePath = localSyncPath + filePath + fileName;
                String absoluteCloudPath = filePath + fileRecord.getEncryptedName();

                if (!Files.exists(Paths.get(absoluteFilePath))) {
                    if (!Files.exists(Paths.get(fileDirectory))) {
                        File fileFolder = new File(fileDirectory);
                        fileFolder.mkdirs();
                    }
                    // dosyayı dropbox üzerinden bilgisayara indirir
                    OutputStream downloadedFile = new FileOutputStream(new File(absoluteFilePath));
                    DropboxClient.files().downloadBuilder(absoluteCloudPath).download(downloadedFile);
                    downloadedFile.close();

                    // dosyanın şifresini çözer
                    byte[] fileBytes = Crypto.decryptFile(Files.readAllBytes(Paths.get(absoluteFilePath)), fileRecord.getKey());

                    // şifreli dosya içeriğini çözülmüş yeni içerik ile değiştirir
                    new FileOutputStream(absoluteFilePath).close();
                    downloadedFile = new FileOutputStream(new File(absoluteFilePath));
                    downloadedFile.write(fileBytes);
                    downloadedFile.close();
                    FILE_RECORD_OPERATIONS.updateDownloadStatus(filePath, fileName, true);
                    FILE_RECORD_OPERATIONS.updateSyncStatus(filePath, fileName, true);
                    NOTIFICATION_OPERATIONS.insert(filePath + fileName + " dosyası başarı ile indirildi!");
                }
            } catch (DbxException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static final void uploadFile(String uploadDirectory, File file) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("mac") >= 0 || os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
        } else {
            uploadDirectory = uploadDirectory.replace("/", "\\");
        }

        String fileDirectory = localSyncPath + uploadDirectory;
        try {
            String fileWithPath = fileDirectory + file.getName();
            if (!Files.exists(Paths.get(fileWithPath))) {
                if (!Files.exists(Paths.get(fileDirectory))) {
                    File fileFolder = new File(fileDirectory);
                    fileFolder.mkdirs();
                }

                try {
                    InputStream is = new FileInputStream(file);
                    Path destinationPath = (Path) Paths.get(fileWithPath);
                    Files.copy(is, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
    }

    public static String getHash(String path, String fileName) {
        byte[] inputBytes;
        try {
            inputBytes = Files.readAllBytes((Path) Paths.get(localSyncPath + path + fileName));
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(inputBytes);
            byte[] digestedBytes = messageDigest.digest();

            String digestString = Base64.getUrlEncoder().encodeToString(digestedBytes);
            return digestString;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void changeSyncStatus(CustomTableView item, boolean newStatus) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (newStatus == true) {
                    if (!item.hasFileChanged()) {
                        FILE_RECORD_OPERATIONS.updateSyncStatus(item.getFilePath(), item.getFileName(), true);
                    } else {
                        try {
                            FileRecord record = FILE_RECORD_OPERATIONS.getByPathAndName(item.getFilePath(),
                                    item.getFileName());
                            EncryptedFileData encryptedFileData = Crypto
                                    .encryptFile(new File(localSyncPath + record.getPath() + record.getName()));
                            String absoluteFilePath = localSyncPath + record.getPath() + record.getName();
                            String newlocalFileModificationDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                                    .format(new File(absoluteFilePath).lastModified());
                            String newFileSize = String.valueOf(Files.size(Paths.get(absoluteFilePath)) / 1024) + " KB";
                            String newHash = getHash(record.getPath(), record.getName());

                            DropboxClient.files().uploadBuilder(record.getPath() + record.getEncryptedName())
                                    .withMode(WriteMode.OVERWRITE)
                                    .withAutorename(false)
                                    .uploadAndFinish(encryptedFileData.getEncryptedFile());

                            FILE_RECORD_OPERATIONS.updateKey(record.getPath(), encryptedFileData.getAesKey(), record.getEncryptedName());
                            FILE_RECORD_OPERATIONS.updateModificationDate(record.getPath(), newlocalFileModificationDate, record.getEncryptedName());
                            FILE_RECORD_OPERATIONS.updateFileSize(record.getPath(), newFileSize, record.getEncryptedName());
                            FILE_RECORD_OPERATIONS.updateHash(record.getPath(), newHash, record.getEncryptedName());

                            DropboxClient.files().moveV2(record.getPath() + record.getEncryptedName(), record.getPath() + encryptedFileData.getEncryptedName());
                            FILE_RECORD_OPERATIONS.updateEncryptedName(record.getPath(), encryptedFileData.getEncryptedName(), record.getEncryptedName());

                            FILE_RECORD_OPERATIONS.updateSyncStatus(item.getFilePath(), item.getFileName(), true);
                            FILE_RECORD_OPERATIONS.updateChangeStatus(record.getPath(), record.getName(), false);
                            NOTIFICATION_OPERATIONS.insert(record.getPath() + record.getName() + " dosyasının içeriği bulutta güncellendi!");
                        } catch (DbxException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    FILE_RECORD_OPERATIONS.updateSyncStatus(item.getFilePath(), item.getFileName(), false);
                }
            }
        }).start();
    }

    public static void shareFile(ObservableList<CustomTableView> fileList, List<String> userEmailList) {
        try {
            String filePath = fileList.get(0).getFilePath();
            String myPrivateKey = Constants.ACCOUNT.privateRSAKey;
            for (String recieverEmail : userEmailList) {
                String recieverPublicKey = Constants.userInformationOperations.getByEmail(recieverEmail).getPublicKey();
                for (CustomTableView shareFile : fileList) {
                    FileRecord file = FILE_RECORD_OPERATIONS.getByPathAndName(filePath, shareFile.getFileName());
                    String fileAESKey = file.getKey();
                    String encryptedAES = Crypto.KEY_EXCHANGE.encryptWithPrivate(fileAESKey, myPrivateKey);
                    String AESfirstPart = encryptedAES.substring(0, 200);
                    String AESsecondPart = encryptedAES.substring(200, encryptedAES.length());
                    String secondEncryptedAES1 = Crypto.KEY_EXCHANGE.encryptWithPublic(AESfirstPart, recieverPublicKey);
                    String secondEncryptedAES2 = Crypto.KEY_EXCHANGE.encryptWithPublic(AESsecondPart, recieverPublicKey);
                    String encryptedFileName = FILE_RECORD_OPERATIONS.getByPathAndName(shareFile.getFilePath(), shareFile.getFileName()).getEncryptedName();
                    String senderEmail = Constants.ACCOUNT.userEmail;
                    SharedFile sharedFile = new SharedFile(recieverEmail, senderEmail, encryptedFileName, secondEncryptedAES1, secondEncryptedAES2, file.getModificationDate(), file.getHash(), file.getFileSize());

                    List<MemberSelector> member = new ArrayList<>();
                    member.add(MemberSelector.email(recieverEmail));

                    FileInputStream sharedFileCredentials = ConfigController.SharedFileCredentials.Save(sharedFile);
                    try {
                        DropboxClient.files().uploadBuilder("/sharing/" + recieverEmail + "+" + file.getEncryptedName() + ".json").uploadAndFinish(sharedFileCredentials);
                    } catch (UploadErrorException e) {
                        NOTIFICATION_OPERATIONS.insert("Seçili dosya " + recieverEmail + " ile zaten paylaşılmış durumda!");
                        Files.delete(Paths.get(Constants.ACCOUNT.cacheSharedFileDirectory + file.getEncryptedName() + ".json"));
                        return;
                    }
                    DropboxClient.client.sharing().addFileMember("/sharing/" + recieverEmail + "+" + file.getEncryptedName() + ".json", member);
                    Files.delete(Paths.get(Constants.ACCOUNT.cacheSharedFileDirectory + file.getEncryptedName() + ".json"));
                    
                    FILE_RECORD_OPERATIONS.updateSharedAccounts(userEmailList, shareFile.getFilePath(), shareFile.getFileName());
                    DropboxClient.client.sharing().addFileMember(file.getPath() + file.getEncryptedName(), member);
                    NOTIFICATION_OPERATIONS.insert(shareFile.getFilePath() + shareFile.getFileName() + " dosyası " + recieverEmail + " ile paylaşıldı!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            NOTIFICATION_OPERATIONS.insert("Dosya Paylaşım Hatası!");
        }
    }

    public static void deleteFile(CustomTableView file) {
        if (file.getFileSyncStatus()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileOperations.deleteFromCloud(file.getFilePath(), file.getFileName());
                }
            }).start();
            FileOperations.deleteFromLocal(file.getFilePath(), file.getFileName());
        } else {
            FileOperations.deleteFromLocal(file.getFilePath(), file.getFileName());
        }
    }

    private static void deleteFromCloud(String path, String fileName) {
        try {
            FileRecord file = FILE_RECORD_OPERATIONS.getByPathAndName(path, fileName);
            DropboxClient.client.files().deleteV2(path + file.getEncryptedName());
            FILE_RECORD_OPERATIONS.delete(fileName, path);
            NOTIFICATION_OPERATIONS.insert(path + fileName + " dosyası buluttan silindi!");
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFromLocal(String path, String fileName) {
        String filePath = localSyncPath;
        String os = System.getProperty("os.name").toLowerCase();

        if (os.indexOf("mac") >= 0 || os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
            filePath += "/" + path;
        } else {
            filePath += "\\" + path;
        }
        filePath += fileName;

        try {
            if (Files.deleteIfExists((Path) Paths.get(filePath))) {
                if (FILE_RECORD_OPERATIONS.getByPathAndName(path, fileName) != null) {
                    FILE_RECORD_OPERATIONS.updateDownloadStatus(path, fileName, false);
                }
                NOTIFICATION_OPERATIONS.insert(path + fileName + " dosyası yerelden silindi!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
