package org.uludag.bmb.beans.database.sharing;

public class RecievedFile {
    private String key;
    private String encryptedName;
    private String decryptedName;

    public RecievedFile(String encryptedName, String decryptedName, String key) {
        this.encryptedName = encryptedName;
        this.decryptedName = decryptedName;
        this.key = key;
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
