package org.uludag.bmb.beans.database.sharing;

public class RecievedFile {
    private String key;
    private String senderEmail;
    private String encryptedName;
    private String decryptedName;

    public RecievedFile(String senderEmail, String encryptedName, String decryptedName, String key) {
        this.senderEmail = senderEmail;
        this.encryptedName = encryptedName;
        this.decryptedName = decryptedName;
        this.key = key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSenderEmail() {
        return this.senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getDecryptedName(){
        return this.decryptedName;
    }

    public void setDecryptedName(String decryptedName){
        this.decryptedName = decryptedName;
    }

    public String getEncryptedName() {
        return this.encryptedName;
    }

    public void setEncryptedName(String encryptedName) {
        this.encryptedName = encryptedName;
    }

    public String getKey() {
        return this.key;
    }

    public void setSecondDecryptedKey(String secondDecryptedKey) {
        this.key = secondDecryptedKey;
    }

}
