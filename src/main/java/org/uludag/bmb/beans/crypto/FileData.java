package org.uludag.bmb.beans.crypto;

import java.io.InputStream;

public class FileData {
    public InputStream encryptedFile;
    public String encryptedFileName;
    public String secretKey;
    public String id;


    public FileData(){

    }

    public FileData(InputStream encryptedFile, String encryptedFileName, String secretKey, String id) {
        this.encryptedFile = encryptedFile;
        this.encryptedFileName = encryptedFileName;
        this.secretKey = secretKey;
        this.id = id;
    }
    
    
}
