package org.uludag.bmb.gui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

public class UploadFile {
    

    public void uploadFileFunc(String computerPath) throws IOException{
        DbxCredential credential;
        try {
            credential = DbxCredential.Reader.readFromFile("authinfo.json");
        } catch (JsonReader.FileLoadException e) {
            return;
        }

        DbxRequestConfig requestConfig = new DbxRequestConfig("dbproject/1.0-SNAPSHOT");
        DbxClientV2 client = new DbxClientV2(requestConfig, credential);

        try {
            try (InputStream in = new FileInputStream(computerPath)) {
                FileMetadata metadata = client.files().uploadBuilder("/Test_1/aaa.txt")
                    .uploadAndFinish(in);
            }
        } catch (DbxException exception) {
            System.err.println(exception.getMessage());
        }

    }
}
