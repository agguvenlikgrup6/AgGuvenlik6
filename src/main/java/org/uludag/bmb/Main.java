package org.uludag.bmb;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.uludag.bmb.oauth.OAuthFlow;
import org.uludag.bmb.oauth.OAuthHttpServer;

public class Main {
    private static final Object LOCK = new Object();
    
    public static void main(String[] args) throws Exception {
        // // org.uludag.bmb.oauth.OAuthLogin.login();
        // OAuthHttpServer server = new OAuthHttpServer();
        // server.start();

        OAuthFlow flow = new OAuthFlow();
        flow.startFlow();

        // org.uludag.bmb.gui.App.main(args);
        // System.out.println(PropertiesReader.getProperty("test123"));
    }

}
