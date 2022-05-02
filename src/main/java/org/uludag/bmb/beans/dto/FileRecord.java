package org.uludag.bmb.beans.dto;

import java.sql.Date;

public class FileRecord {
    private String name;
    private String path;
    private String key;
    private Date modificationDate;
    private String hash;
    private String encryptedName;
    private boolean sync;
}
