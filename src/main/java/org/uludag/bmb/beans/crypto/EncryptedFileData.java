package org.uludag.bmb.beans.crypto;

import java.io.InputStream;

import com.dropbox.core.v2.files.FileMetadata;

public class EncryptedFileData {
    public InputStream encryptedFile;
    public String name;
    public String key;
    public FileMetadata metadata;
    
    public EncryptedFileData() {

    }

    public EncryptedFileData(InputStream file, String name, String secretKey) {
        this.encryptedFile = file;
        this.name = name;
        
    }

    public EncryptedFileData(FileMetadata metadata, String name, String key) {
        this.metadata = metadata;
        this.name = name;
        this.key = key;
    }
}
