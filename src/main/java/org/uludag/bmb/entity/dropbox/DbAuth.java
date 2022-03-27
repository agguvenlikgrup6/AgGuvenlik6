package org.uludag.bmb.entity.dropbox;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.TokenAccessType;
import com.dropbox.core.json.JsonReader.FileLoadException;

import org.uludag.bmb.PropertiesReader;

public class DbAuth {
    private DbxRequestConfig requestConfig;
    private DbxAppInfo appInfo;
    private String sessionKey;
    private String redirectUri;
    private DbxStandardSessionStore session;

    public DbAuth() throws IOException, FileLoadException {
        requestConfig = new DbxRequestConfig(PropertiesReader.getProperty("clientIdentifier"));
        appInfo = new DbxAppInfo("bxvnh4y5x2ar0jz");
        sessionKey = this.generateSessionKey();
        redirectUri = PropertiesReader.getProperty("redirectUri");
    }

    public DbAuth(HttpServletRequest request) throws IOException, FileLoadException {
        this();
        session = new DbxStandardSessionStore(request.getSession(), this.sessionKey);
    }

    public DbxAppInfo getAppInfo() {
        return appInfo;
    }

    private String generateSessionKey() {
        return UUID.randomUUID().toString();
    }

    public String getSessionKey() {
        return this.sessionKey;
    }

    public DbxSessionStore getSession() {
        return this.session;
    }

    public String getRedirectUri() {
        return this.redirectUri;
    }

    public DbxRequestConfig getRequestConfig() {
        return this.requestConfig;
    }

    public DbxWebAuth.Request buildWebAuthRequest() {
        return DbxWebAuth.newRequestBuilder()
                .withRedirectUri(getRedirectUri(), this.session)
                .withTokenAccessType(TokenAccessType.OFFLINE)
                .withForceReapprove(false)
                .build();
    }

}
