package org.uludag.bmb;

import java.io.IOException;
import java.net.URISyntaxException;
import com.dropbox.core.DbxException;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException, DbxException {
        // org.uludag.bmb.gui.App.main(args);
        // org.uludag.bmb.oauth.OAuthLogin.login();
        System.out.println(PropertiesReader.getProperty("test123"));
    }
}
