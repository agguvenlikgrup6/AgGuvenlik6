package org.uludag.bmb;

import org.uludag.bmb.httpserver.AuthCommand;
import org.uludag.bmb.httpserver.AuthHttpServer;
import org.uludag.bmb.httpserver.ServerConfiguration;

public class Main {
    public static void main(String[] args) throws Exception {
        org.uludag.bmb.oauth.OAuthLogin.login();
        
        

        // org.uludag.bmb.gui.App.main(args);
        // System.out.println(PropertiesReader.getProperty("test123"));
    }
}
