package org.uludag.bmb.operations.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.json.JsonReader.FileLoadException;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;

import org.uludag.bmb.PropertiesReader;

public class DbClient {
    public static final DbxClientV2 client = DbClient.getClient();

    private static DbxClientV2 getClient() {
        try {
            DbAuth auth = new DbAuth();
            DbxCredential credential = DbxCredential.Reader.readFromFile(PropertiesReader.getProperty("authinfo"));
            DbxClientV2 client = new DbxClientV2(auth.getRequestConfig(), credential);
            client.users().getCurrentAccount();
            return client;
        } catch (DbxException | FileLoadException e) {
            e.printStackTrace();
            return null;
        }
    }
}
