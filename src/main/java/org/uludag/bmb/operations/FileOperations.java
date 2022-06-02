package org.uludag.bmb.operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.beans.database.sharing.SharedFile;
import org.uludag.bmb.beans.dataproperty.CustomTableView;
import org.uludag.bmb.operations.dropbox.Client;
import org.uludag.bmb.service.cryption.Crypto;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.sharing.MemberSelector;

import javafx.collections.ObservableList;

public class FileOperations {
    public static final void downloadFile(String filePath, String fileName) {
        new Thread(() -> {
            try {
                String localSyncPath = Constants.CONFIG.localSyncPath;
                String fileWithPath = localSyncPath + filePath + fileName;

                if (!Files.exists(Paths.get(fileWithPath))) {
                    if (!Files.exists(Paths.get(localSyncPath + filePath))) {
                        File fileFolder = new File(localSyncPath + filePath);
                        fileFolder.mkdirs();
                    }
                    FileRecord record = Constants.fileRecordOperations.getByPathAndName(filePath, fileName);
                    OutputStream downloadFile = new FileOutputStream(localSyncPath + filePath + record.getEncryptedName());
                    Client.client.files().downloadBuilder(filePath + record.getEncryptedName()).download(downloadFile);
                    Constants.fileRecordOperations.updateDownloadStatus(filePath, fileName, true);
                    Constants.fileRecordOperations.updateSyncStatus(filePath, fileName, true);
                    downloadFile.close();
                    String decryptedName = Crypto.decryptName(
                            Base64.getUrlDecoder().decode(record.getEncryptedName().getBytes(StandardCharsets.UTF_8)),
                            record.getKey());
                    OutputStream decryptedFile = new FileOutputStream(localSyncPath + filePath + decryptedName);
                    byte[] fileBytes = Crypto.decryptFile(
                            Files.readAllBytes(Paths.get(localSyncPath + filePath + record.getEncryptedName())),
                            record.getKey());
                    decryptedFile.write(fileBytes);
                    decryptedFile.close();

                    Files.delete(Paths.get(localSyncPath + filePath + record.getEncryptedName()));

                    Constants.notificationOperations
                            .insert(filePath + fileName + " dosyası başarı ile indirildi!");
                }
            } catch (DbxException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static final void deleteFile(String path, String fileName) {
        String localSyncPath = Constants.CONFIG.localSyncPath;
        String filePath = localSyncPath;
        String os = System.getProperty("os.name").toLowerCase();
        if (!path.equals("/")) {
            if (os.indexOf("mac") >= 0 || os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
                filePath += "/";
            } else {
                filePath += "\\";
            }
        }
        filePath += path + fileName;

        File file = new File(filePath);
        file.delete();
    }

    public static final void uploadFile(String uploadDirectory, File file) {
        String localSyncPath = Constants.CONFIG.localSyncPath;
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
        String localSyncPath = Constants.CONFIG.localSyncPath;
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

    public static void changeSyncStatus(CustomTableView item, boolean status) {
        if (status) {
            String localSyncPath = Constants.CONFIG.localSyncPath;
            localSyncPath += item.getFilePath() + item.getFileName();
            if (item.hasFileChanged()) {
                try {
                    InputStream is = new FileInputStream(new File(localSyncPath));
                    deleteFromLocal(item.getFilePath(), item.getFileName());
                    deleteFromCloud(item.getFilePath(), item.getFileName());
                    Path destinationPath = (Path) Paths.get(localSyncPath);
                    Files.copy(is, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                FileRecord record = Constants.fileRecordOperations.getByPathAndName(item.getFilePath(),
                        item.getFileName());
                File file = new File(localSyncPath);
                if (!record.getModificationDate()
                        .equals(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(file.lastModified()))) {
                    item.setChangeStatus(true);
                    Constants.fileRecordOperations.updateChangeStatus(item.getFilePath(), item.getFileName(), true);
                    changeSyncStatus(item, status);
                }
            }
            Constants.fileRecordOperations.updateSyncStatus(item.getFilePath(), item.getFileName(), true);
        } else {
            Constants.fileRecordOperations.updateSyncStatus(item.getFilePath(), item.getFileName(), false);
        }
    }

    public static boolean shareFile(ObservableList<CustomTableView> fileList, List<String> userEmailList) {
        try {
            String filePath = fileList.get(0).getFilePath();
            String myPrivateKey = Constants.CONFIG.privateRSAKey;
            for (String recieverEmail : userEmailList) {
                String recieverPublicKey = Constants.userInformationOperations.getByEmail(recieverEmail).getPublicKey();
                for (CustomTableView shareFile : fileList) {
                    FileRecord file = Constants.fileRecordOperations.getByPathAndName(filePath,
                            shareFile.getFileName());
                    String fileAESKey = file.getKey();
                    String encryptedAES = Crypto.KEY_EXCHANGE.encryptWithPrivate(fileAESKey, myPrivateKey);
                    String AESfirstPart = encryptedAES.substring(0, 200);
                    String AESsecondPart = encryptedAES.substring(200, encryptedAES.length());
                    String secondEncryptedAES1 = Crypto.KEY_EXCHANGE.encryptWithPublic(AESfirstPart, recieverPublicKey);
                    String secondEncryptedAES2 = Crypto.KEY_EXCHANGE.encryptWithPublic(AESsecondPart,
                            recieverPublicKey);
                    String encryptedFileName = Constants.fileRecordOperations
                            .getByPathAndName(shareFile.getFilePath(), shareFile.getFileName())
                            .getEncryptedName();
                    String senderEmail = Constants.CONFIG.userEmail;
                    Constants.sharedFileOperations.insert(new SharedFile(recieverEmail, senderEmail, encryptedFileName, secondEncryptedAES1,secondEncryptedAES2));

                    List<MemberSelector> member = new ArrayList<>();
                    member.add(MemberSelector.email(recieverEmail));
                    Constants.fileRecordOperations.updateSharedAccounts(userEmailList, shareFile.getFilePath(), shareFile.getFileName());
                    Client.client.sharing().addFileMember(file.getPath() + file.getEncryptedName(), member);
                    Constants.notificationOperations.insert(shareFile.getFilePath() + shareFile.getFileName() + " dosyası " + recieverEmail + " ile paylaşıldı!");
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Constants.notificationOperations.insert("Dosya Paylaşım Hatası!");
            return false;
        }
    }

    public static void deleteFile(CustomTableView file) {
        if (file.getFileSyncStatus()) {
            FileOperations.deleteFromCloud(file.getFilePath(), file.getFileName());
        } else {
            FileOperations.deleteFromLocal(file.getFilePath(), file.getFileName());
        }
    }

    private static void deleteFromCloud(String path, String fileName) {
        try {
            FileRecord file = Constants.fileRecordOperations.getByPathAndName(path, fileName);
            Client.client.files().deleteV2(path + file.getEncryptedName());
            Constants.fileRecordOperations.delete(fileName, path);
            FileOperations.deleteFile(path, fileName);
            Constants.notificationOperations.insert(path + fileName + " dosyası buluttan ve yerelden silindi!");
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    private static void deleteFromLocal(String path, String fileName) {
        String localSyncPath = Constants.CONFIG.localSyncPath;
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
                if (Constants.fileRecordOperations.getByPathAndName(path, fileName) != null) {
                    Constants.fileRecordOperations.updateDownloadStatus(path, fileName, false);
                }
                Constants.notificationOperations.insert(path + fileName + " dosyası yerelden silindi!");
            } else {
                Constants.notificationOperations
                        .insert(path + fileName + " dosyası yerelde bulunmadığı için silinemedi!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
