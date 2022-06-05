package org.uludag.bmb.beans.database.sharing;

public class RecievedFile {
    private String fileKey;
    private String senderEmail;
    private String encryptedName;
    private String modificationDate;
    private String hash;
    private String fileSize;
    private String decryptedName;
    private int id;

    public RecievedFile(String senderEmail, String encryptedName, String decryptedName, String fileKey,
            String modificationDate, String hash, String fileSize) {
        this.senderEmail = senderEmail;
        this.encryptedName = encryptedName;
        this.decryptedName = decryptedName;
        this.modificationDate = modificationDate;
        this.hash = hash;
        this.fileSize = fileSize;
        this.fileKey = fileKey;
    }

    public RecievedFile(int id, String senderEmail, String encryptedName, String decryptedName, String fileKey) {
        this.id = id;
        this.senderEmail = senderEmail;
        this.encryptedName = encryptedName;
        this.decryptedName = decryptedName;
        this.fileKey = fileKey;
    }

    public RecievedFile() {

    }


    public String getModificationDate() {
        return this.modificationDate;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }


    public String getFileKey() {
        return this.fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getSenderEmail() {
        return this.senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getDecryptedName() {
        return this.decryptedName;
    }

    public void setDecryptedName(String decryptedName) {
        this.decryptedName = decryptedName;
    }

    public String getEncryptedName() {
        return this.encryptedName;
    }

    public void setEncryptedName(String encryptedName) {
        this.encryptedName = encryptedName;
    }

    public void setSecondDecryptedKey(String secondDecryptedKey) {
        this.fileKey = secondDecryptedKey;
    }

}
