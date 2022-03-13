package org.uludag.bmb.oauth;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxPKCEWebAuth;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.TokenAccessType;

import org.uludag.bmb.PropertiesReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Pkce {
    public DbxAuthFinish authorize(DbxAppInfo appInfo) throws IOException {
        DbxRequestConfig requestConfig = new DbxRequestConfig(PropertiesReader.getProperty("clientIdentifier"));
        DbxAppInfo appInfoWithoutSecret = new DbxAppInfo(appInfo.getKey());
        DbxPKCEWebAuth pkceWebAuth = new DbxPKCEWebAuth(requestConfig, appInfoWithoutSecret);

        DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder()
                .withNoRedirect()
                .withTokenAccessType(TokenAccessType.OFFLINE)
                .build();

        String authorizeUrl = pkceWebAuth.authorize(webAuthRequest);
        System.out.println("LINK: " + authorizeUrl);

        String code = new BufferedReader(new InputStreamReader(System.in)).readLine();
        if (code == null) {
            System.exit(1);
        }
        code = code.trim();

        try {
            return pkceWebAuth.finishFromCode(code);
        } catch (DbxException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
            return null;
        }
    }

}
