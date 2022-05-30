package org.uludag.bmb.beans.database;

public class SharedFile {
    private String email;
    private String encryptedName;
    private String fileKeyPart1;
    private String fileKeyPart2;

    public SharedFile(String email, String encryptedName, String fileKeyPart1, String fileKeyPart2) {
        this.email = email;
        this.encryptedName = encryptedName;
        this.fileKeyPart1 = fileKeyPart1;
        this.fileKeyPart2 = fileKeyPart2;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncryptedName() {
        return this.encryptedName;
    }

    public void setEncryptedName(String encryptedName) {
        this.encryptedName = encryptedName;
    }

    public String getFileKeyPart1() {
        return this.fileKeyPart1;
    }

    public void setFileKeyPart1(String fileKeyPart1) {
        this.fileKeyPart1 = fileKeyPart1;
    }

    public String getFileKeyPart2() {
        return this.fileKeyPart2;
    }

    public void setFileKeyPart2(String fileKeyPart2) {
        this.fileKeyPart2 = fileKeyPart2;
    }

}
