package org.uludag.bmb.operations.dropbox;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.DeleteResult;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.sharing.MemberSelector;

import org.uludag.bmb.beans.config.Config;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.beans.dataproperty.TableViewDataProperty;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.operations.database.FileRecordOperations;
import org.uludag.bmb.operations.database.NotificationOperations;
import org.uludag.bmb.operations.database.PublicInfoOperations;
import org.uludag.bmb.service.cryption.Crypto;

import javafx.collections.ObservableList;

public class FileOperations {
    private static final FileRecordOperations fileRecordOperations = new FileRecordOperations();
    private static final NotificationOperations notificationOperations = new NotificationOperations();
    private static final PublicInfoOperations publicInfoOperations = new PublicInfoOperations();

    public static final void DOWNLOAD_FILE(String localPath, String filePath, String fileName) {
        new Thread(() -> {
            try {
                String fileWithPath = localPath + filePath + fileName;

                if (!Files.exists(Paths.get(fileWithPath))) {
                    if (!Files.exists(Paths.get(localPath + filePath))) {
                        File fileFolder = new File(localPath + filePath);
                        fileFolder.mkdirs();
                    }
                    FileRecord record = fileRecordOperations.getByPathAndName(filePath, fileName);
                    OutputStream downloadFile = new FileOutputStream(localPath + filePath + record.getEncryptedName());
                    Client.client.files().downloadBuilder(filePath + record.getEncryptedName()).download(downloadFile);
                    fileRecordOperations.UPDATE_DOWNLOAD_STATUS(filePath, fileName, true);
                    fileRecordOperations.UPDATE_SYNC_STATUS(filePath, fileName, true);
                    downloadFile.close();
                    String decryptedName = Crypto.decryptName(
                            Base64.getUrlDecoder().decode(record.getEncryptedName().getBytes(StandardCharsets.UTF_8)),
                            record.getKey());
                    OutputStream decryptedFile = new FileOutputStream(localPath + filePath + decryptedName);
                    byte[] fileBytes = Crypto.decryptFile(
                            Files.readAllBytes(Paths.get(localPath + filePath + record.getEncryptedName())),
                            record.getKey());
                    decryptedFile.write(fileBytes);
                    decryptedFile.close();

                    Files.delete(Paths.get(localPath + filePath + record.getEncryptedName()));

                    notificationOperations.insertNotification(filePath + fileName + " dosyası başarı ile indirildi!");
                }
            } catch (DbxException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static final void DELETE_FILE(String path, String fileName) {
        Config config = ConfigController.Settings.LoadSettings();
        String localPath = config.getLocalDropboxPath();
        String filePath = localPath;
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

    public static final void UPLOAD_FILE(String uploadDirectory, File file) {
        Config config = ConfigController.Settings.LoadSettings();
        String localPath = config.getLocalDropboxPath();
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("mac") >= 0 || os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
        } else {
            uploadDirectory = uploadDirectory.replace("/", "\\");
        }

        String fileDirectory = localPath + uploadDirectory;
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

    public static void DELETE_FROM_CLOUD(String path, String fileName) {
        try {
            FileRecord file = fileRecordOperations.getByPathAndName(path, fileName);
            DeleteResult ds = Client.client.files().deleteV2(path + file.getEncryptedName());
            fileRecordOperations.DELETE(fileName, path);
            FileOperations.DELETE_FILE(path, fileName);
            notificationOperations.insertNotification(path + fileName + " dosyası buluttan ve yerelden silindi!");
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    public static void DELETE_FROM_LOCAL(String path, String fileName) {
        Config config = ConfigController.Settings.LoadSettings();
        String localPath = config.getLocalDropboxPath();
        String filePath = localPath;
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
                    fileRecordOperations.UPDATE_DOWNLOAD_STATUS(path, fileName, false);
                }
                notificationOperations.insertNotification(path + fileName + " dosyası yerelden silindi!");
            } else {
                notificationOperations
                        .insertNotification(path + fileName + " dosyası yerelde bulunmadığı için silinemedi!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String GET_HASH(String path, String fileName) {
        String localPath = ConfigController.Settings.LoadSettings().getLocalDropboxPath();
        byte[] inputBytes;
        try {
            inputBytes = Files.readAllBytes((Path) Paths.get(localPath + path + fileName));
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

    public static void CHANGE_STATUS(TableViewDataProperty item, boolean status) {
        if (status) {
            String filePath = ConfigController.Settings.LoadSettings().getLocalDropboxPath();
            filePath += item.getFilePath() + item.getFileName();
            if (item.getChangeStatus()) {
                try {
                    InputStream is = new FileInputStream(new File(filePath));
                    DELETE_FROM_LOCAL(item.getFilePath(), item.getFileName());
                    DELETE_FROM_CLOUD(item.getFilePath(), item.getFileName());
                    Path destinationPath = (Path) Paths.get(filePath);
                    Files.copy(is, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                FileRecord record = fileRecordOperations.getByPathAndName(item.getFilePath(), item.getFileName());
                File file = new File(filePath);
                if (!record.getModificationDate()
                        .equals(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(file.lastModified()))) {
                    item.setChangeStatus(true);
                    fileRecordOperations.UPDATE_CHANGE_STATUS(item.getFilePath(), item.getFileName(), true);
                    CHANGE_STATUS(item, status);
                }
            }
            fileRecordOperations.UPDATE_SYNC_STATUS(item.getFilePath(), item.getFileName(), true);
        } else {
            fileRecordOperations.UPDATE_SYNC_STATUS(item.getFilePath(), item.getFileName(), false);
        }
    }

    public static FileMetadata GET_METADATA(String filePath, String fileName) {
        FileRecord record = fileRecordOperations.getByPathAndName(filePath,
                fileName);
        ListFolderResult result;
        try {
            if (filePath.equals("/")) {
                filePath = "";
            }
            result = Client.client.files().listFolder(filePath);

            List<Metadata> entries = result.getEntries();
            for (Metadata metadata : entries) {
                if (metadata instanceof FileMetadata &&
                        metadata.getName().equals(record.getEncryptedName())) {
                    FileMetadata data = (FileMetadata) metadata;
                    return data;

                }
            }
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean SHARE_FILE(ObservableList<TableViewDataProperty> fileList, List<String> userEmailList) {
        try {
            String filePath = fileList.get(0).getFilePath();
            String myPrivateKey = publicInfoOperations.getPrivateKey();
            for (String recieverEmail : userEmailList) {
                String recieverPublicKey = publicInfoOperations.getUserPublicKey(recieverEmail);
                for (TableViewDataProperty shareFile : fileList) {
                    FileRecord file = fileRecordOperations.getByPathAndName(filePath, shareFile.getFileName());
                    String fileAESKey = file.getKey();
                    String encryptedAES = Crypto.KEY_EXCHANGE.encryptWithPrivate(fileAESKey, myPrivateKey);
                    String AESfirstPart = encryptedAES.substring(0, 490);
                    String AESsecondPart = encryptedAES.substring(490, encryptedAES.length());
                    String secondEncryptedAES1 = Crypto.KEY_EXCHANGE.encryptWithPublic(AESfirstPart, recieverPublicKey);
                    String secondEncryptedAES2 = Crypto.KEY_EXCHANGE.encryptWithPublic(AESsecondPart,
                            recieverPublicKey);
                    String encryptedFileName = fileRecordOperations
                            .getByPathAndName(shareFile.getFilePath(), shareFile.getFileName())
                            .getEncryptedName();
                    publicInfoOperations.insertSharedFileKey(recieverEmail, secondEncryptedAES1, secondEncryptedAES2,
                            encryptedFileName);

                    List<MemberSelector> member = new ArrayList<>();
                    member.add(MemberSelector.email(recieverEmail));
                    Client.client.sharing().addFileMember(file.getPath() + file.getEncryptedName(), member);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
