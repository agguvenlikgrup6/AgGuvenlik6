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

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;

import org.uludag.bmb.beans.config.Config;
import org.uludag.bmb.beans.crypto.EncryptedFileData;
import org.uludag.bmb.beans.dropbox.DbClient;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.cryption.Crypto;

public class DbxFiles {
    public static final void DOWNLOAD_FILE(String localPath, String filePath, String fileName) {
        try {
            OutputStream downloadFile = new FileOutputStream(localPath + filePath + fileName);
            try {
                DbClient client = new DbClient(true);
                client.getClient().files().downloadBuilder("/" + filePath + fileName).download(downloadFile);
            } finally {
                downloadFile.close();
                System.out.println("indirme başarılı");
            }
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
    }

    public static final void UPLOAD_FILE(String uploadDirectory, File file) {
        // Önce dosyasının takip dizinine kopyalanması
        // ardından yükleme işleminin dizin içerisinden gerçekleştirilmesi
        Config config = ConfigController.Settings.LoadSettings();
        String destinationFile = config.getLocalDropboxPath();

        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("mac") >= 0) {
            destinationFile += "/";
        } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
            destinationFile += "/";
        } else {
            destinationFile += "\\";
        }
        destinationFile += file.getName();
        try {
            InputStream is = new FileInputStream(file);
            Path desPath = (Path) Paths.get(destinationFile);
            Files.copy(is, desPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            // TODO: handle exception
        }

        DbClient dbClient = new DbClient(true);

        EncryptedFileData efd = Crypto.encryptFile(file);
        try {
            FileMetadata metaData = dbClient.getClient().files().uploadBuilder(uploadDirectory + efd.name)
                    .uploadAndFinish(efd.encryptedFile);
            ConfigController.Crypto.Save(new EncryptedFileData(metaData, file.getName(), efd.key));
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
    }
}
