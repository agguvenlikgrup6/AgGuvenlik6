package org.uludag.bmb.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        DbxRequestConfig requestConfig = new DbxRequestConfig("bmb4016grup6");
        DbxAppInfo appInfoWithoutSecret = new DbxAppInfo(appInfo.getKey());
        DbxPKCEWebAuth pkceWebAuth = new DbxPKCEWebAuth(requestConfig, appInfoWithoutSecret);

        DbxWebAuth.Request webAuthRequest =  DbxWebAuth.newRequestBuilder()
            .withNoRedirect()
            .withTokenAccessType(TokenAccessType.OFFLINE)
            .withForceReapprove(false)
            .build();

        String authorizeUrl = pkceWebAuth.authorize(webAuthRequest);

        try {
            new ProcessBuilder("x-www-browser", authorizeUrl).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

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