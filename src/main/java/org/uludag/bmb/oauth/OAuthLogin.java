package org.uludag.bmb.oauth;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.oauth.DbxCredential;

public class OAuthLogin {
    public static void login() throws IOException {
        URL argAppInfoFile = OAuthLogin.class.getResource("/app.json");
        String argAuthFileOutput = "authinfo.json";

        DbxAppInfo appInfo;
        try {
            appInfo = DbxAppInfo.Reader.readFromFile(argAppInfoFile.getPath());
        } catch (JsonReader.FileLoadException ex) {
            System.err.println("Error reading <app-info-file>: " + ex.getMessage());
            System.exit(1);
            return;
        }

        DbxAuthFinish authFinish = null;
        authFinish = new Pkce().authorize(appInfo);

        DbxCredential credential = new DbxCredential(authFinish.getAccessToken(), authFinish
                .getExpiresAt(), authFinish.getRefreshToken(), appInfo.getKey(), appInfo.getSecret());

        File output = new File(argAuthFileOutput);
        try {
            DbxCredential.Writer.writeToFile(credential, output);
            System.out.println("Saved authorization information to \"" + output.getCanonicalPath() + "\".");
        } catch (IOException ex) {
            System.err.println("Error saving to <auth-file-out>: " + ex.getMessage());
            System.err.println("Dumping to stderr instead:");
            DbxCredential.Writer.writeToStream(credential, System.err);
            System.exit(1);
            return;
        }
    }
}
