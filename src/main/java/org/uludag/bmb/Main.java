package org.uludag.bmb;

import org.uludag.bmb.httpserver.AuthHttpServer;
import org.uludag.bmb.httpserver.AuthStart;
import org.uludag.bmb.httpserver.PKCEAuthFlow;

public class Main {
    public static void main(String[] args) throws Exception {
        // org.uludag.bmb.oauth.OAuthLogin.login();
        AuthHttpServer server = new AuthHttpServer();
        server.start();

        PKCEAuthFlow flow = new PKCEAuthFlow(true);
        // org.uludag.bmb.gui.App.main(args);
        // System.out.println(PropertiesReader.getProperty("test123"));
    }
}
