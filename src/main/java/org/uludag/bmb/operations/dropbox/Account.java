package org.uludag.bmb.operations.dropbox;

import java.io.IOException;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.json.JsonReader.FileLoadException;
import com.dropbox.core.oauth.DbxCredential;

import org.uludag.bmb.PropertiesReader;

public class Account {
    private DbxCredential credential;
    private DbxRequestConfig requestConfig;

    public Account() {
        try {
            credential = DbxCredential.Reader.readFromFile(PropertiesReader.getProperty("authinfo"));
            requestConfig = new DbxRequestConfig(PropertiesReader.getProperty("clientIdentifier"));
        } catch (FileLoadException | IOException e) {
            e.printStackTrace();
        }
    }

    public DbxCredential getCredential() {
        return credential;
    }

    public DbxRequestConfig getRequestConfig() throws IOException {
        return requestConfig;
    }

}
