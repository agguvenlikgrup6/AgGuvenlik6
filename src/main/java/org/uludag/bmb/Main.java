package org.uludag.bmb;

import org.uludag.bmb.oauth.OAuthFlow;
import org.uludag.bmb.oauth.OAuthHttpServer;

public class Main {
    public static void main(String[] args) throws Exception {
        // org.uludag.bmb.oauth.OAuthLogin.login();
        OAuthHttpServer server = new OAuthHttpServer();
        server.start();

        OAuthFlow flow = new OAuthFlow(true);
        // org.uludag.bmb.gui.App.main(args);
        // System.out.println(PropertiesReader.getProperty("test123"));
    }
}
