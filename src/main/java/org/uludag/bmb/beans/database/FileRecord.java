package org.uludag.bmb.beans.database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

// import java.sql.Date;

public class FileRecord {
    private String name;
    private String path;
    private String key;
    private String modificationDate;
    private String hash;
    private String encryptedName;
    private int sync;

    public FileRecord() {

    }

    public FileRecord(String name, String path, String key, String modificationDate, String hash, String encryptedName,
            int sync) {
        this.name = name;
        this.path = path;
        this.key = key;
        this.modificationDate = modificationDate;
        this.hash = hash;
        this.encryptedName = encryptedName;
        this.sync = sync;
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

    public String getModificationDate(){
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

}
