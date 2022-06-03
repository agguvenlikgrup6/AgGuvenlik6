package org.uludag.bmb.beans.crypto;

import java.io.InputStream;

public class EncryptedFileData {
    private InputStream encryptedFile;
    private String encryptedName;
    private String aesKey;

    public EncryptedFileData(InputStream file, String name, String secretKey) {
        this.encryptedFile = file;
        this.encryptedName = name;
        this.aesKey = secretKey;
    }

    public InputStream getEncryptedFile() {
        return this.encryptedFile;
    }

    public String getEncryptedName() {
        return this.encryptedName;
    }

    public String getAesKey() {
        return this.aesKey;
    }
}
