package org.uludag.bmb.beans.constants;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.operations.database.FileRecordOperations;
import org.uludag.bmb.operations.database.NotificationOperations;
import org.uludag.bmb.operations.database.SharedFileOperations;
import org.uludag.bmb.operations.database.TableOperations;
import org.uludag.bmb.operations.database.UserInformationOperations;

public class Constants {
    public static final FileRecordOperations fileRecordOperations = new FileRecordOperations();
    public static final NotificationOperations notificationOperations = new NotificationOperations();
    public static final SharedFileOperations sharedFileOperations = new SharedFileOperations();
    public static final UserInformationOperations userInformationOperations = new UserInformationOperations();
    public static final TableOperations tableOperations = new TableOperations();

    public class TABLES {
        public static final String fileRecords = PropertiesReader.getProperty("table_Record");
        public static final String notification = PropertiesReader.getProperty("table_Notification");
        public static final String userInformation = PropertiesReader.getProperty("table_userInformation");
        public static final String sharedFiles = PropertiesReader.getProperty("table_sharedFiles");
        public static final String recievedFiles = PropertiesReader.getProperty("table_recievedFiles");  
    }
}
