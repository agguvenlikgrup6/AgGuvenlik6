package org.uludag.bmb.beans.database;

public class FileRecord {
    private String name;
    private String path;
    private String key;
    private String modificationDate;
    private String hash;
    private String encryptedName;
    private int sync;
    private int changeStatus;
    private int downloadStatus;
    private String fileSize;
    private String sharedAccounts;

    public FileRecord() {

    }

    public FileRecord(int downloadStatus, String name, String path, String key, String modificationDate, String hash,
            String encryptedName, int sync, int changeStatus, String fileSize, String sharedAccounts) {
        this.downloadStatus = downloadStatus;
        this.name = name;
        this.path = path;
        this.key = key;
        this.modificationDate = modificationDate;
        this.hash = hash;
        this.encryptedName = encryptedName;
        this.sync = sync;
        this.changeStatus = changeStatus;
        this.fileSize = fileSize;
        this.sharedAccounts = sharedAccounts;
    }

    public String getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getSharedAccounts() {
        return this.sharedAccounts;
    }

    public void setSharedAccounts(String sharedAccounts) {
        this.sharedAccounts = sharedAccounts;
    }

    public FileRecord(String encryptedName, String filePath) {
        this.encryptedName = encryptedName;
        this.path = filePath;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getEncryptedName() {
        return this.encryptedName;
    }

    public void setEncryptedName(String encryptedName) {
        this.encryptedName = encryptedName;
    }

    public int getSync() {
        return this.sync;
    }

    public void setSync(int sync) {
        this.sync = sync;
    }

    public int getChangeStatus() {
        return this.changeStatus;
    }

    public void setChangeStatus(int changeStatus) {
        this.changeStatus = changeStatus;
    }

    public int getDownloadStatus() {
        return this.downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }
}
