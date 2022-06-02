package org.uludag.bmb.operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
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
import org.uludag.bmb.beans.constants.Constants.CONFIG;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.beans.database.sharing.SharedFile;
import org.uludag.bmb.beans.dataproperty.CustomTableView;
import org.uludag.bmb.operations.database.FileRecordOperations;
import org.uludag.bmb.operations.database.NotificationOperations;
import org.uludag.bmb.operations.dropbox.DropboxClient;
import org.uludag.bmb.service.cryption.Crypto;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.sharing.MemberSelector;

import javafx.collections.ObservableList;

public class FileOperations {
    private static final String localSyncPath = CONFIG.localSyncPath;
    private static final FileRecordOperations FILE_RECORD_OPERATIONS = Constants.fileRecordOperations;
    private static final NotificationOperations NOTIFICATION_OPERATIONS = Constants.notificationOperations;
    // public static final SharedFileOperations sharedFileOperations =
    // Constants.sharedFileOperations;
    // public static final UserInformationOperations userInformationOperations =
    // Constants.userInformationOperations;
    // public static final TableOperations tableOperations =
    // Constants.tableOperations;

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
                    byte[] fileBytes = Crypto.decryptFile(Files.readAllBytes(Paths.get(absoluteFilePath)),
                            fileRecord.getKey());
                    
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

    public static final void deleteFile(String path, String fileName) {
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

    public static void changeSyncStatus(CustomTableView item, boolean status) {
        if (status) {
            String filaPath = localSyncPath;
            filaPath += item.getFilePath() + item.getFileName();
            if (item.hasFileChanged()) {
                try {
                    InputStream is = new FileInputStream(new File(filaPath));
                    deleteFromLocal(item.getFilePath(), item.getFileName());
                    deleteFromCloud(item.getFilePath(), item.getFileName());
                    Path destinationPath = (Path) Paths.get(filaPath);
                    Files.copy(is, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                FileRecord record = FILE_RECORD_OPERATIONS.getByPathAndName(item.getFilePath(),
                        item.getFileName());
                File file = new File(filaPath);
                if (!record.getModificationDate()
                        .equals(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(file.lastModified()))) {
                    item.setChangeStatus(true);
                    FILE_RECORD_OPERATIONS.updateChangeStatus(item.getFilePath(), item.getFileName(), true);
                    changeSyncStatus(item, status);
                }
            }
            FILE_RECORD_OPERATIONS.updateSyncStatus(item.getFilePath(), item.getFileName(), true);
        } else {
            FILE_RECORD_OPERATIONS.updateSyncStatus(item.getFilePath(), item.getFileName(), false);
        }
    }

    public static boolean shareFile(ObservableList<CustomTableView> fileList, List<String> userEmailList) {
        try {
            String filePath = fileList.get(0).getFilePath();
            String myPrivateKey = Constants.CONFIG.privateRSAKey;
            for (String recieverEmail : userEmailList) {
                String recieverPublicKey = Constants.userInformationOperations.getByEmail(recieverEmail).getPublicKey();
                for (CustomTableView shareFile : fileList) {
                    FileRecord file = FILE_RECORD_OPERATIONS.getByPathAndName(filePath,
                            shareFile.getFileName());
                    String fileAESKey = file.getKey();
                    String encryptedAES = Crypto.KEY_EXCHANGE.encryptWithPrivate(fileAESKey, myPrivateKey);
                    String AESfirstPart = encryptedAES.substring(0, 200);
                    String AESsecondPart = encryptedAES.substring(200, encryptedAES.length());
                    String secondEncryptedAES1 = Crypto.KEY_EXCHANGE.encryptWithPublic(AESfirstPart, recieverPublicKey);
                    String secondEncryptedAES2 = Crypto.KEY_EXCHANGE.encryptWithPublic(AESsecondPart,
                            recieverPublicKey);
                    String encryptedFileName = FILE_RECORD_OPERATIONS
                            .getByPathAndName(shareFile.getFilePath(), shareFile.getFileName())
                            .getEncryptedName();
                    String senderEmail = Constants.CONFIG.userEmail;
                    Constants.sharedFileOperations.insert(new SharedFile(recieverEmail, senderEmail, encryptedFileName,
                            secondEncryptedAES1, secondEncryptedAES2));

                    List<MemberSelector> member = new ArrayList<>();
                    member.add(MemberSelector.email(recieverEmail));
                    FILE_RECORD_OPERATIONS.updateSharedAccounts(userEmailList, shareFile.getFilePath(),
                            shareFile.getFileName());
                    DropboxClient.client.sharing().addFileMember(file.getPath() + file.getEncryptedName(), member);
                    NOTIFICATION_OPERATIONS.insert(shareFile.getFilePath() + shareFile.getFileName() + " dosyası "
                            + recieverEmail + " ile paylaşıldı!");
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            NOTIFICATION_OPERATIONS.insert("Dosya Paylaşım Hatası!");
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
            FileRecord file = FILE_RECORD_OPERATIONS.getByPathAndName(path, fileName);
            DropboxClient.client.files().deleteV2(path + file.getEncryptedName());
            FILE_RECORD_OPERATIONS.delete(fileName, path);
            FileOperations.deleteFile(path, fileName);
            NOTIFICATION_OPERATIONS.insert(path + fileName + " dosyası buluttan ve yerelden silindi!");
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    private static void deleteFromLocal(String path, String fileName) {
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
            } else {
                NOTIFICATION_OPERATIONS
                        .insert(path + fileName + " dosyası yerelde bulunmadığı için silinemedi!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
