package org.uludag.bmb.oauth;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxPKCEWebAuth;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.TokenAccessType;

public class Pkce {
    public DbxAuthFinish authorize(DbxAppInfo appInfo) throws IOException, URISyntaxException {
        // Run through Dropbox API authorization process without client secret
        DbxRequestConfig requestConfig = new DbxRequestConfig("examples-authorize");
        DbxAppInfo appInfoWithoutSecret = new DbxAppInfo(appInfo.getKey());
        DbxPKCEWebAuth pkceWebAuth = new DbxPKCEWebAuth(requestConfig, appInfoWithoutSecret);

        DbxWebAuth.Request webAuthRequest =  DbxWebAuth.newRequestBuilder()
            .withNoRedirect()
            .withTokenAccessType(TokenAccessType.ONLINE)
            .build();

        String authorizeUrl = pkceWebAuth.authorize(webAuthRequest);

        try {
            new ProcessBuilder("x-www-browser", authorizeUrl).start();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        //     Desktop.getDesktop().browse(new URI(authorizeUrl));
        // }

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