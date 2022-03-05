package org.uludag.bmb4016.auth;

import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxPKCEWebAuth;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.TokenAccessType;
import com.dropbox.core.DbxAppInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;


public class PkceAuthorize {
    public DbxAuthFinish authorize(DbxAppInfo appInfo) throws IOException, URISyntaxException {
        // Run through Dropbox API authorization process without client secret
        DbxRequestConfig requestConfig = new DbxRequestConfig("examples-authorize");
        DbxAppInfo appInfoWithoutSecret = new DbxAppInfo(appInfo.getKey());
        DbxPKCEWebAuth pkceWebAuth = new DbxPKCEWebAuth(requestConfig, appInfoWithoutSecret);

        DbxWebAuth.Request webAuthRequest =  DbxWebAuth.newRequestBuilder()
            .withNoRedirect()
            .withTokenAccessType(TokenAccessType.OFFLINE)
            .build();

        String authorizeUrl = pkceWebAuth.authorize(webAuthRequest);

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(authorizeUrl));
        }

        System.out.println("1. Bağlantıya gidin: " + authorizeUrl);
        System.out.print("Auth kodunu giriniz: ");

        String code = new BufferedReader(new InputStreamReader(System.in)).readLine();

        if (code == null) {
            System.exit(1);
        }
        code = code.trim();

        try {
            return pkceWebAuth.finishFromCode(code);
        } catch (DbxException ex) {
            System.err.println("Yetkilendirme hatası: " + ex.getMessage());
            System.exit(1); return null;
        }
    }
}