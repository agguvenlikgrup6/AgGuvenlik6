package org.uludag.bmb.oauth;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import java.awt.Desktop;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxPKCEWebAuth;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.TokenAccessType;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.oauth.DbxCredential;

import org.springframework.mock.web.MockHttpServletRequest;
import org.uludag.bmb.PropertiesReader;

import com.sun.net.httpserver.HttpServer;

public class OAuthFlow {
    private DbxAppInfo appInfo;

    public void startFlow() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        URL appInfoFile = OAuthFlow.class.getResource("/app.json");
        try {
            assert appInfoFile != null;
            appInfo = DbxAppInfo.Reader.readFromFile(appInfoFile.getPath());
        } catch (JsonReader.FileLoadException ex) {
            System.err.println("Error reading <app-info-file>: " + ex.getMessage());
            System.exit(1);
            return;
        }

        String sessionKey = "dropbox-auth-csrf-token";

        DbxSessionStore session = new DbxStandardSessionStore(request.getSession(), sessionKey);
        String redirectUri = PropertiesReader.getProperty("redirectUri");

        DbxRequestConfig requestConfig = new DbxRequestConfig(PropertiesReader.getProperty("clientIdentifier"));
        DbxAppInfo appInfoWithoutSecret = new DbxAppInfo(appInfo.getKey());

        DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder()
                .withRedirectUri(redirectUri, session)
                .withTokenAccessType(TokenAccessType.OFFLINE)
                .withForceReapprove(false)
                .build();

        DbxPKCEWebAuth pkceWebAuth = new DbxPKCEWebAuth(requestConfig, appInfoWithoutSecret);
        String authorizeUrl = pkceWebAuth.authorize(webAuthRequest);

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(authorizeUrl));

                var latch = new CountDownLatch(1);
                var server = HttpServer.create(new InetSocketAddress(PropertiesReader.getProperty("host"),
                        Integer.parseInt(PropertiesReader.getProperty("port"))), 0);

                server.createContext(PropertiesReader.getProperty("context"), exchange -> {
                    try {
                        String redirectQuery = exchange.getRequestURI().getQuery();

                        DbxAuthFinish authFinish = pkceWebAuth.finishFromRedirect(
                                PropertiesReader.getProperty("redirectUri"),
                                session,
                                RedirectParamsMapper.params(
                                        "code", RedirectParamsMapper.extractQueryParam(redirectQuery, "code"),
                                        "state", RedirectParamsMapper.extractQueryParam(redirectQuery, "state")));

                        String response = "";

                        
                        response += "<html>";
                        response += "<head><title>" + "Dropbox Auth" + "</title></head>";
                        response += "<h1>Giriş İşlemi Başarılı, Sayfayı Kapatabilirsiniz</h1>";
                        response += "</body>";
                        response += "</html>";

                        exchange.sendResponseHeaders(200, 0);
                        BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody());
                        byte[] bs = response.getBytes(StandardCharsets.UTF_8); 
                        try (ByteArrayInputStream bis = new ByteArrayInputStream(bs)) {
                            byte[] buffer = new byte[bs.length];
                            int count;
                            while ((count = bis.read(buffer)) != -1) {
                                out.write(buffer, 0, count);
                            }
                            out.close();
                        }

                        // exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
                        DbxCredential credential = new DbxCredential(authFinish.getAccessToken(), authFinish
                                .getExpiresAt(), authFinish.getRefreshToken(), appInfo.getKey(), appInfo.getSecret());

                        File output = new File("authinfo.json");
                        DbxCredential.Writer.writeToFile(credential, output);
                        System.out.println("Saved authorization information to \"" + output.getCanonicalPath() + "\".");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    latch.countDown();
                });
                server.start();

                // TODO #11 FXML İÇERİSİNDE ACCESS TOKEN BEKLENDİĞİNE DAİR BİLGİLENDİRME YAP
                // System.out.println("Waiting for code");

                latch.await(Integer.parseInt(PropertiesReader.getProperty("timeout")), TimeUnit.SECONDS);
                server.stop(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
