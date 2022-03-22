package org.uludag.bmb.oauth;

import java.io.IOException;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

import org.junit.Test;

public class OAuthFlowTest {
    @Test
    public void checkConnection() throws IOException {
        DbxCredential credential = null;
        try {
            credential = DbxCredential.Reader.readFromFile("authinfo.json");
        } catch (JsonReader.FileLoadException e) {
            System.err.println("Error loading auth file: " + e.getMessage());
        }

        DbxRequestConfig requestConfig = new DbxRequestConfig("dbproject/1.0-SNAPSHOT");
        DbxClientV2 dbxClient = new DbxClientV2(requestConfig, credential); 
        dbxClient.check();

        FullAccount dbxAccountInfo = null;
        try {
            dbxAccountInfo = dbxClient.users().getCurrentAccount();
        } catch (DbxException ex) {
            System.err.println("Error making API call: " + ex.getMessage());
            return;
        }
        System.out.println(dbxAccountInfo.toStringMultiline());
    }
}
