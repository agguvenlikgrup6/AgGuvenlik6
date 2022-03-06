package org.uludag.bmb4016.auth;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.json.JsonReader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AuthLogin {
    public static void login() throws IOException, URISyntaxException {
        Logger.getLogger("").setLevel(Level.WARNING);
        
        //TODO KEY VE SECRET BİLGİSİ GÜVENLİ BİR ŞEKİLDE OTOMATİZE EDİLECEK 

        String argAppInfoFile = "/home/oguz/Documents/DropboxEncryption/grup6/src/main/java/org/uludag/bmb4016/auth/app.json";

        DbxAppInfo appInfo;
        try {
            appInfo = DbxAppInfo.Reader.readFromFile(argAppInfoFile);
        } catch (JsonReader.FileLoadException ex) {
            System.err.println("Error reading <app-info-file>: " + ex.getMessage());
            System.exit(1); return;
        }

        DbxAuthFinish authFinish = null;

        authFinish = new PkceAuthorize().authorize(appInfo);

        System.out.println("Giriş başarılı.");
        System.out.println("- User ID: " + authFinish.getUserId());
        System.out.println("- Account ID: " + authFinish.getAccountId());
        System.out.println("- Access Token: " + authFinish.getAccessToken());
        System.out.println("- Refresh Token: " + authFinish.getRefreshToken());
        System.out.println("- Yetki Kapsamı: " + authFinish.getScope());

    }
}
