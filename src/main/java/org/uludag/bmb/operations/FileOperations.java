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
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.uludag.bmb.beans.constants.Constants;
import org.uludag.bmb.beans.constants.Constants.ACCOUNT;
import org.uludag.bmb.beans.crypto.EncryptedFileData;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.beans.database.sharing.SharedFile;
import org.uludag.bmb.beans.dataproperty.CustomTableData;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.operations.database.FileRecordOperations;
import org.uludag.bmb.operations.database.NotificationOperations;
import org.uludag.bmb.operations.dropbox.DropboxClient;
import org.uludag.bmb.service.cryption.Crypto;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.AccessLevel;
import com.dropbox.core.v2.sharing.MemberSelector;
import com.dropbox.core.v2.sharing.UserFileMembershipInfo;

import javafx.collections.ObservableList;

public class FileOperations {
    private static final String localSyncPath = ACCOUNT.localSyncPath;
    private static final FileRecordOperations fileRecordOperations = Constants.fileRecordOperations;
    private static final NotificationOperations NOTIFICATION_OPERATIONS = Constants.notificationOperations;

    public static final void downloadFile(String cloudPath, String fileName) {
        new Thread(() -> {
            try {
                FileRecord fileRecord = fileRecordOperations.getByPathAndName(cloudPath, fileName);

                String fileDirectory = localSyncPath + cloudPath;
                String absoluteFilePath = localSyncPath + cloudPath + fileName;
                String absoluteCloudPath = cloudPath + fileRecord.getEncryptedName();

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
                    new FileOutputStream(absoluteFilePath).close(); // dosyanın içeriğini sıfırlamak için
                    downloadedFile = new FileOutputStream(new File(absoluteFilePath));
                    downloadedFile.write(fileBytes);
                    downloadedFile.close();
                    fileRecordOperations.updateDownloadStatus(cloudPath, fileName, true);
                    fileRecordOperations.updateSyncStatus(cloudPath, fileName, true);
                    NOTIFICATION_OPERATIONS.insert(cloudPath + fileName + " dosyası başarı ile indirildi!");
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

    public static String getHash(String cloudPath, String fileName) {
        byte[] inputBytes;
        try {
            inputBytes = Files.readAllBytes((Path) Paths.get(localSyncPath + cloudPath + fileName));
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

    public static String getHash(String text) {
        byte[] inputBytes;
        try {
            inputBytes = text.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(inputBytes);
            byte[] digestedBytes = messageDigest.digest();

            String digestString = Base64.getUrlEncoder().encodeToString(digestedBytes);
            return digestString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void changeSyncStatus(CustomTableData selectedFile, boolean newSyncStatus) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (newSyncStatus == true) {
                    if (!selectedFile.hasFileChanged()) {
                        fileRecordOperations.updateSyncStatus(selectedFile.getFilePath(), selectedFile.getFileName(), true);
                    } else {
                        try {
                            fileRecordOperations.updateBusyStatus(1, selectedFile.getFilePath(), selectedFile.getFileName());
                            FileRecord outdatedRecord = fileRecordOperations.getByPathAndName(selectedFile.getFilePath(), selectedFile.getFileName());
                            EncryptedFileData newCrypto = Crypto.encryptFile(new File(localSyncPath + outdatedRecord.getPath() + outdatedRecord.getName()));
                            String newEncryptedName = newCrypto.getEncryptedName();
                            String absoluteFilePath = localSyncPath + outdatedRecord.getPath() + outdatedRecord.getName();
                            String newlocalFileModificationDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new File(absoluteFilePath).lastModified());
                            String newFileSize = String.valueOf(Files.size(Paths.get(absoluteFilePath)) / 1024) + " KB";
                            String newHash = getHash(outdatedRecord.getPath(), outdatedRecord.getName());

                            // eski şifreli isme sahip olan dosyanın içeriği yeni şifreli bilgi ile
                            // güncellenir
                            DropboxClient.files().uploadBuilder(outdatedRecord.getPath() + outdatedRecord.getEncryptedName()).withMode(WriteMode.OVERWRITE).withAutorename(false).uploadAndFinish(newCrypto.getEncryptedFile());
                            // dosyanın ismi yeni şifreli isim ile güncellenir
                            DropboxClient.files().moveV2(outdatedRecord.getPath() + outdatedRecord.getEncryptedName(), outdatedRecord.getPath() + newEncryptedName);

                            // yerel dosya kaydı yeni veriler ile güncellenir
                            fileRecordOperations.updateEncryptedName(outdatedRecord.getPath(), newEncryptedName, outdatedRecord.getEncryptedName());
                            fileRecordOperations.updateKey(outdatedRecord.getPath(), newCrypto.getAesKey(), outdatedRecord.getEncryptedName());
                            fileRecordOperations.updateModificationDate(outdatedRecord.getPath(), newlocalFileModificationDate, outdatedRecord.getEncryptedName());
                            fileRecordOperations.updateFileSize(outdatedRecord.getPath(), newFileSize, outdatedRecord.getEncryptedName());
                            fileRecordOperations.updateHash(outdatedRecord.getPath(), newHash, outdatedRecord.getEncryptedName());
                            fileRecordOperations.updateSyncStatus(selectedFile.getFilePath(), selectedFile.getFileName(), true);
                            fileRecordOperations.updateChangeStatus(outdatedRecord.getPath(), outdatedRecord.getName(), false);

                            fileRecordOperations.updateBusyStatus(0, selectedFile.getFilePath(), selectedFile.getFileName());

                            FileRecord updatedRecord = fileRecordOperations.getByPathAndName(selectedFile.getFilePath(), selectedFile.getFileName());
                            String oldEncryptedName = outdatedRecord.getEncryptedName();
                            List<Metadata> allJSONFiles = DropboxClient.files().listFolder("/sharing").getEntries();
                            for (Metadata jsonMetadata : allJSONFiles) {
                                FileMetadata sharedFileMetadata = (FileMetadata) jsonMetadata;
                                // eski şifreli isme sahip ilişkili json dosyaları
                                if (sharedFileMetadata.getName().contains(oldEncryptedName)) {
                                    List<UserFileMembershipInfo> fileMembers = DropboxClient.sharing().listFileMembers(sharedFileMetadata.getId()).getUsers();

                                    // DropboxClient.files().deleteV2(sharedFileMetadata.getPathDisplay());
                                    for (UserFileMembershipInfo member : fileMembers) {
                                        if (member.getAccessType().equals(AccessLevel.VIEWER)) {
                                            String recieverEmail = member.getUser().getEmail();
                                            FileInputStream newJSONFile = createJSONFile(updatedRecord, selectedFile, member.getUser().getEmail());
                                            DropboxClient.files().uploadBuilder("/sharing/" + recieverEmail + "+" + oldEncryptedName + ".json").withMode(WriteMode.OVERWRITE).withAutorename(false).uploadAndFinish(newJSONFile);
                                            DropboxClient.files().moveV2("/sharing/" + recieverEmail + "+" + oldEncryptedName + ".json", "/sharing/" + recieverEmail + "+" + newEncryptedName + ".json");
                                            newJSONFile.close();
                                        }
                                    }
                                }
                            }

                            NOTIFICATION_OPERATIONS.insert(outdatedRecord.getPath() + outdatedRecord.getName() + " dosyasının içeriği bulutta güncellendi!");
                        } catch (DbxException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    fileRecordOperations.updateSyncStatus(selectedFile.getFilePath(), selectedFile.getFileName(), false);
                }
            }
        }).start();
    }

    public static void shareFile(FileRecord fileRecord, String userEmail) {
        shareFile(new CustomTableData(fileRecord.getDownloadStatus(), fileRecord.getName(), fileRecord.getModificationDate(), fileRecord.getPath(), fileRecord.getSync(), fileRecord.getChangeStatus(), fileRecord.getFileSize(), Arrays.asList(fileRecord.getSharedAccounts().split(";")),
                fileRecord.getSharedAccounts()), userEmail);
    }

    public static void shareFile(CustomTableData selectedFile, String newUserEMail) {
        try {
            for (String sharedEmail : selectedFile.getSharedAccounts()) {
                if (sharedEmail.equals(newUserEMail)) {
                    NOTIFICATION_OPERATIONS.insert("HATA! " + selectedFile.getFileName() + " dosyası " + newUserEMail + " ile zaten paylaşılmış durumda!");
                    return;
                }
            }

            List<MemberSelector> member = new ArrayList<>();
            member.add(MemberSelector.email(newUserEMail));
            FileRecord fileRecord = fileRecordOperations.getByPathAndName(selectedFile.getFilePath(), selectedFile.getFileName());
            FileInputStream sharedJSON = createJSONFile(fileRecord, selectedFile, newUserEMail);

            DropboxClient.files().upload("/sharing/" + newUserEMail + "+" + fileRecord.getEncryptedName() + ".json").uploadAndFinish(sharedJSON);
            sharedJSON.close();
            DropboxClient.client.sharing().addFileMember("/sharing/" + newUserEMail + "+" + fileRecord.getEncryptedName() + ".json", member);
            
            Files.delete(Paths.get(Constants.ACCOUNT.cacheSharedFileDirectory + fileRecord.getEncryptedName() + ".json"));

            fileRecordOperations.updateSharedAccount(newUserEMail, selectedFile.getFilePath(), selectedFile.getFileName());

            DropboxClient.client.sharing().addFileMember(fileRecord.getPath() + fileRecord.getEncryptedName(), member);
            NOTIFICATION_OPERATIONS.insert(selectedFile.getFilePath() + selectedFile.getFileName() + " dosyası " + newUserEMail + " ile paylaşıldı!");

        } catch (Exception e) {
            e.printStackTrace();
            NOTIFICATION_OPERATIONS.insert("Dosya Paylaşım Hatası!");
        }
    }

    private static FileInputStream createJSONFile(FileRecord file, CustomTableData shareFile, String recieverEmail) {
        String priavateRsaKey = Constants.ACCOUNT.privateRSAKey;
        String recieverPublicKey = Constants.userInformationOperations.getByEmail(recieverEmail).getPublicKey();
        String aesKey = file.getKey();

        String encryptedAES = Crypto.KEY_EXCHANGE.encryptWithPrivate(aesKey, priavateRsaKey);
        String encryptedAES_p1 = encryptedAES.substring(0, 200);
        String encryptedAES_p2 = encryptedAES.substring(200, encryptedAES.length());

        String encrypted_p1 = Crypto.KEY_EXCHANGE.encryptWithPublic(encryptedAES_p1, recieverPublicKey);
        String encrypted_p2 = Crypto.KEY_EXCHANGE.encryptWithPublic(encryptedAES_p2, recieverPublicKey);

        String encryptedFileName = fileRecordOperations.getByPathAndName(shareFile.getFilePath(), shareFile.getFileName()).getEncryptedName();
        String senderEmail = Constants.ACCOUNT.userEmail;
        String fileHash = getHash(file.getPath() + file.getName());
        SharedFile sharedFile = new SharedFile(recieverEmail, senderEmail, encryptedFileName, encrypted_p1, encrypted_p2, file.getModificationDate(), file.getHash(), file.getFileSize(), fileHash);

        return ConfigController.SharedFileCredentials.Save(sharedFile);
    }

    public static void shareFileBatch(ObservableList<CustomTableData> fileList, List<String> userEmailList) {
        try {
            for (String recieverEmail : userEmailList) {
                for (CustomTableData shareFile : fileList) {
                    shareFile(shareFile, recieverEmail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(CustomTableData file) {
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
            FileRecord file = fileRecordOperations.getByPathAndName(path, fileName);
            DropboxClient.client.files().deleteV2(path + file.getEncryptedName());
            fileRecordOperations.delete(fileName, path);
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
                if (fileRecordOperations.getByPathAndName(path, fileName) != null) {
                    fileRecordOperations.updateDownloadStatus(path, fileName, false);
                }
                NOTIFICATION_OPERATIONS.insert(path + fileName + " dosyası yerelden silindi!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
