package org.uludag.bmb.operations.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.json.JsonReader.FileLoadException;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DbxUserFilesRequests;

import org.uludag.bmb.PropertiesReader;

public class DropboxClient {
    public static DbxClientV2 client = DropboxClient.getClient();

    public static DbxClientV2 getClient() {
        try {
            ClientUtils auth = new ClientUtils();
            DbxCredential credential = DbxCredential.Reader.readFromFile(PropertiesReader.getProperty("authinfo"));
            DbxClientV2 client = new DbxClientV2(auth.getRequestConfig(), credential);
            client.users().getCurrentAccount();
            return client;
        } catch (DbxException | FileLoadException e) {
            return null;
        }
    }

    public static DbxUserFilesRequests files(){
        return client.files();
    }
}
