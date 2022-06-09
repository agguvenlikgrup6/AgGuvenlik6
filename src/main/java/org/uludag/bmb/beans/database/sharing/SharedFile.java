package org.uludag.bmb.beans.database.sharing;

public class SharedFile {
    private String recieverEmail;
    private String senderEmail;
    private String encryptedName;
    private String modificationDate;
    private String hash;
    private String fileSize;
    private String fileKeyPart1;
    private String fileKeyPart2;
    private String pathHash;

    public SharedFile(String recieverEmail, String senderEmail, String encryptedName, String fileKeyPart1,
            String fileKeyPart2, String modificationDate, String hash, String fileSize, String pathHash) {
        this.recieverEmail = recieverEmail;
        this.senderEmail = senderEmail;
        this.encryptedName = encryptedName;
        this.fileKeyPart1 = fileKeyPart1;
        this.fileKeyPart2 = fileKeyPart2;
        this.modificationDate = modificationDate;
        this.fileSize = fileSize;
        this.hash = hash;
        this.pathHash = pathHash;
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


    public String getPathHash() {
        return this.pathHash;
    }

    public void setPathHash(String pathHash) {
        this.pathHash = pathHash;
    }


    public SharedFile() {

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
