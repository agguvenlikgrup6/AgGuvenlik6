package org.uludag.bmb.oauth;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.json.JsonReader;
import java.net.URL;

import java.io.IOException;
import java.net.URISyntaxException;

public class OAuthLogin {
    public static void login() throws IOException, URISyntaxException {      
        URL file = OAuthLogin.class.getResource("/app.json");

        DbxAppInfo appInfo;
        try {
            appInfo = DbxAppInfo.Reader.readFromFile(file.getPath());
        } catch (JsonReader.FileLoadException ex) {
            System.err.println("Dosya okuma hatası: " + ex.getMessage());
            System.exit(1); return;
        }

        DbxAuthFinish authFinish = null;

        authFinish = new Pkce().authorize(appInfo);

        System.out.println("Giriş başarılı.");
        System.out.println("- Kullanıcı ID: " + authFinish.getUserId());
        System.out.println("- Hesap ID: " + authFinish.getAccountId());
        System.out.println("- Access Token: " + authFinish.getAccessToken());
        System.out.println("- Refresh Token: " + authFinish.getRefreshToken());
        System.out.println("- Yetki Kapsamı: " + authFinish.getScope());

    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        login();
    }
}
