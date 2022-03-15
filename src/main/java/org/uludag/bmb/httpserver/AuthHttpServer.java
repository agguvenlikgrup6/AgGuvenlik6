package org.uludag.bmb.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.sun.net.httpserver.HttpServer;

import org.uludag.bmb.oauth.Response;

public class AuthHttpServer {
    final ServerConfiguration config;
    public Response response;


    @Inject
    public AuthHttpServer(ServerConfiguration config) {
        this.config = config;
    }

    String getCode(){
        try {
            var latch = new CountDownLatch(1);
            var server = HttpServer.create(new InetSocketAddress(config.getHost(), config.getPort()), 0);
            server.createContext(config.getContext(), exchange -> {
                this.response.setCode(exchange.getRequestURI().toString().split("&")[0].split("=")[1]);
                System.out.println("code = " + this.response);

                var response = "";
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
                
                latch.countDown();
            });
            server.start();
            System.out.println("waiting for redirect URI");
            // auth işlemi bitene kadar 60 saniye bekler
            latch.await(config.getTimeout(), TimeUnit.SECONDS);
            server.stop(0);
            return this.response.code;
            
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
