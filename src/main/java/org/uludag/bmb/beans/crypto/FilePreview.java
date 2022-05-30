package org.uludag.bmb.beans.crypto;

public class FilePreview {
    private String recieverEmail;
    private String senderEmail;
    private String encryptedName;
    private String secondDecryptedKey;
    private String decryptedName;

    public FilePreview(String recieverEmail, String senderEmail, String encryptedName, String decryptedName, String secondDecryptedKey) {
        this.recieverEmail = recieverEmail;
        this.senderEmail = senderEmail;
        this.encryptedName = encryptedName;
        this.decryptedName = decryptedName;
        this.secondDecryptedKey = secondDecryptedKey;
    }

    public String getDecryptedName(){
        return this.decryptedName;
    }

    public void setDecryptedName(String decryptedName){
        this.decryptedName = decryptedName;
    }

    public String getRecieverEmail() {
        return this.recieverEmail;
    }

    public void setRecieverEmail(String recieverEmail) {
        this.recieverEmail = recieverEmail;
    }

    public String getSenderEmail() {
        return this.senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getEncryptedName() {
        return this.encryptedName;
    }

    public void setEncryptedName(String encryptedName) {
        this.encryptedName = encryptedName;
    }

    public String getSecondDecryptedKey() {
        return this.secondDecryptedKey;
    }

    public void setSecondDecryptedKey(String secondDecryptedKey) {
        this.secondDecryptedKey = secondDecryptedKey;
    }

}
