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
        Config config = ConfigController.Settings.LoadSettings();
        String destinationFile = config.getLocalDropboxPath();
        destinationFile += uploadDirectory.substring(1, uploadDirectory.length());
        destinationFile += file.getName();
        try {
            InputStream is = new FileInputStream(file);
            Path desPath = (Path) Paths.get(destinationFile);
            Files.copy(is, desPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
