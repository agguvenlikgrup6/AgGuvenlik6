package org.uludag.bmb.operations;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.dropbox.core.DbxException;

import org.uludag.bmb.beans.dropbox.DbClient;

public class DbxFiles extends DbxOperations {
    public static final void DOWNLOAD_FILE(String localPath, String filePath, String fileName) {
        // Thread th = new Thread(() -> {
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

        // });
        // th.start();
    }
}
