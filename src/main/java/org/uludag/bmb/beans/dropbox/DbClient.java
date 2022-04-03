package org.uludag.bmb.beans.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.json.JsonReader.FileLoadException;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;

import org.uludag.bmb.PropertiesReader;

public class DbClient {
    private DbxClientV2 client;
    private DbAuth auth;
    private DbxCredential credential;

    public DbClient() {
        try {
            this.auth = new DbAuth();
        } catch (FileLoadException e) {
            e.printStackTrace();
        }
    }

    public DbClient(boolean loginState){
        this();
        if(loginState){
            this.login();
        }
    }

    public boolean login() {
        try {
            credential = DbxCredential.Reader.readFromFile(PropertiesReader.getProperty("authinfo"));
            this.client = new DbxClientV2(auth.getRequestConfig(), credential);
            this.client.users().getCurrentAccount();
            return true;
        } catch (DbxException | FileLoadException ex) {
            return false;
        }
    }

    public DbxClientV2 getClient() {
        return this.client;
    }
}
