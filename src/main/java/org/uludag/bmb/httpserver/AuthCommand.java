package org.uludag.bmb.httpserver;

import javax.inject.Inject;

public class AuthCommand {
    final AuthHttpServer authHttpServer;

    @Inject
    public AuthCommand(AuthHttpServer authHttpServer) {
        this.authHttpServer = authHttpServer;
    }

    public void httpServerHandler() {
        authHttpServer.start();
    }
}