package org.uludag.bmb;

import org.uludag.bmb.httpserver.AuthHttpServer;
import org.uludag.bmb.httpserver.AuthStart;

public class Main {
    public static void main(String[] args) throws Exception {
        // org.uludag.bmb.oauth.OAuthLogin.login();
        AuthHttpServer server = new AuthHttpServer();
        server.start();

        AuthStart authStart = new AuthStart();
        authStart.startOauthFlow();
        // org.uludag.bmb.gui.App.main(args);
        // System.out.println(PropertiesReader.getProperty("test123"));
    }
}
