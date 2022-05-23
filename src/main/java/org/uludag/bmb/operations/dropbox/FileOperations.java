package org.uludag.bmb.operations.dropbox;

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
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.DeleteResult;

import org.uludag.bmb.beans.config.Config;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.controller.database.DatabaseController;
import org.uludag.bmb.service.cryption.Crypto;

public class FileOperations {
    private static final DatabaseController dc = new DatabaseController();

    public static final void DOWNLOAD_FILE(String localPath, String filePath, String fileName) {
        new Thread(() -> {
            try {
                String fileWithPath = localPath + filePath + fileName;

                if (!Files.exists(Paths.get(fileWithPath))) {
                    if (!Files.exists(Paths.get(localPath + filePath))) {
                        File fileFolder = new File(localPath + filePath);
                        fileFolder.mkdirs();
                    }
                    FileRecord record = dc.getByPathAndName(filePath, fileName);
                    OutputStream downloadFile = new FileOutputStream(localPath + filePath + record.getEncryptedName());
                    Client.client.files().downloadBuilder(filePath + record.getEncryptedName()).download(downloadFile);
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

        if (os.indexOf("mac") >= 0 || os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
            filePath += "/";
        } else {
            filePath += "\\";
        }
        filePath += fileName;

        File file = new File(filePath);
        if (file.delete()) {
            System.out.println("dosya silindi");
        }
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
    }

    public static void DELETE_FROM_CLOUD(String path, String fileName) {
        try {
            FileRecord file = dc.getByPathAndName(path, fileName);
            DeleteResult ds = Client.client.files().deleteV2(path + file.getEncryptedName());
            dc.deleteRecord(fileName, path);
            dc.insertNotification(path + fileName + " dosyası buluttan silindi!");
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
                dc.insertNotification(path + fileName + " dosyası yerelden silindi!");
            } else {
                dc.insertNotification(path + fileName + " dosyası yerelde bulunmadığı için silinemedi!");
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
}
