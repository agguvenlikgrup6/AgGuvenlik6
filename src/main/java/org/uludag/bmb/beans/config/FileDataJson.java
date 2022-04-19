package org.uludag.bmb.beans.config;

public class FileDataJson {
    public String encryptedFileName;
    public String secretKey;
    public String id;


    public FileDataJson(){

    }

    public FileDataJson(String encryptedFileName, String secretKey, String id) {
        this.encryptedFileName = encryptedFileName;
        this.secretKey = secretKey;
        this.id = id;
    }
}
