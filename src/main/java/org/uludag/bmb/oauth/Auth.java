package org.uludag.bmb.oauth;

import java.net.URL;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.json.JsonReader;

public class Auth {
    private String sessionKey;
    private DbxAppInfo appInfo;
    private String redirectUri;
    private String clientIdentifier;


    public String getSessionKey() {
        return this.sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public DbxAppInfo getAppInfo(String resourceName) {
        URL appInfoFile = OAuthFlow.class.getResource("/" + resourceName);
        try {
            assert appInfoFile != null;
            appInfo = DbxAppInfo.Reader.readFromFile(appInfoFile.getPath());
        } catch (JsonReader.FileLoadException ex) {
            System.err.println("Error reading <app-info-file>: " + ex.getMessage());
        }

        return this.appInfo;
    }

    public void setAppInfo(DbxAppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public String getRedirectUri() {
        return this.redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getClientIdentifier() {
        return this.clientIdentifier;
    }

    public void setClientIdentifier(String clientIdentifier) {
        this.clientIdentifier = clientIdentifier;
    }


}
