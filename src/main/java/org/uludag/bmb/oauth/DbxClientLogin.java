package org.uludag.bmb.oauth;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;

public class DbxClientLogin {
    public DbxClientV2 client;

    public DbxClientLogin(){
        DbxCredential credential;
        try {
            credential = DbxCredential.Reader.readFromFile("authinfo.json");
        } catch (JsonReader.FileLoadException e) {
            return;
        }

        DbxRequestConfig requestConfig = new DbxRequestConfig("dbproject/1.0-SNAPSHOT");
        client = new DbxClientV2(requestConfig, credential);
    }
}
