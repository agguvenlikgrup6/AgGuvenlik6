package org.uludag.bmb.operations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;

import org.uludag.bmb.beans.crypto.EncryptedFileData;
import org.uludag.bmb.beans.dropbox.DbClient;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.cryption.Crypto;

public class DbxFiles extends DbxOperations {
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

    public static final void UPLOAD_FILE(String uploadDirectory, File file){
        DbClient dbClient = new DbClient(true);
        
        EncryptedFileData efd = Crypto.encryptFile(file);
        try {
            FileMetadata metaData = dbClient.getClient().files().uploadBuilder(uploadDirectory + efd.name).uploadAndFinish(efd.encryptedFile);
            ConfigController.Crypto.Save(new EncryptedFileData(metaData, efd.name, efd.key));
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
    }

    public static final EncryptedFileData ENCRYPT_FILE(File file){
        return null;

    }

    public static final void DECRYPT_FILE(){

    }
}
