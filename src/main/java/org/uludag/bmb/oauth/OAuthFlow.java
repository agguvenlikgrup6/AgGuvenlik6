package org.uludag.bmb.oauth;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public OAuthValidation authValidation;

    public OAuthFlow() {
        authValidation = new OAuthValidation();
    }

    public void startWithCode() {
        // TODO .finishFromCode seçeneği eklenebilir
    }

    public void startWithRedirect() throws IOException {
        if (!authValidation.isValid()) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            String sessionKey = "dropbox-auth-csrf-token";

            DbxSessionStore session = new DbxStandardSessionStore(request.getSession(), sessionKey);
            String redirectUri = PropertiesReader.getProperty("redirectUri");

            URL appInfoFile = OAuthFlow.class.getResource("/app.json");
            try {
                assert appInfoFile != null;
                appInfo = DbxAppInfo.Reader.readFromFile(appInfoFile.getPath());
            } catch (JsonReader.FileLoadException ex) {
                System.err.println("Error reading <app-info-file>: " + ex.getMessage());
            }

            DbxRequestConfig requestConfig = new DbxRequestConfig(PropertiesReader.getProperty("clientIdentifier"));
            DbxAppInfo appInfoWithoutSecret = new DbxAppInfo(appInfo.getKey());

            DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder()
                    .withRedirectUri(redirectUri, session)
                    .withTokenAccessType(TokenAccessType.OFFLINE)
                    .withForceReapprove(false)
                    .build();

            DbxPKCEWebAuth pkceWebAuth = new DbxPKCEWebAuth(requestConfig, appInfoWithoutSecret);
            String authorizeUrl = pkceWebAuth.authorize(webAuthRequest);

            String os = System.getProperty("os.name").toLowerCase();
            Runtime rt = Runtime.getRuntime();
            if (os.indexOf("mac") >= 0) {
                rt.exec("open " + authorizeUrl);
            } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
                rt.exec("xdg-open " + authorizeUrl);
            } else {
                rt.exec("rundll32 url.dll,FileProtocolHandler " + authorizeUrl);
            }

            try {
                CountDownLatch latch = new CountDownLatch(2);
                HttpServer server = HttpServer.create(new InetSocketAddress(PropertiesReader.getProperty("host"),
                        Integer.parseInt(PropertiesReader.getProperty("port"))), 0);

                server.createContext("/success", new OAuthSuccess(latch));

                server.createContext(PropertiesReader.getProperty("context"), exchange -> {
                    try {
                        String redirectQuery = exchange.getRequestURI().getQuery();

                        DbxAuthFinish authFinish = pkceWebAuth.finishFromRedirect(
                                redirectUri,
                                session,
                                RedirectParamsMapper.params(redirectQuery));

                        DbxCredential credential = new DbxCredential(authFinish.getAccessToken(), authFinish
                                .getExpiresAt(), authFinish.getRefreshToken(), appInfo.getKey());

                        File output = new File("authinfo.json");
                        DbxCredential.Writer.writeToFile(credential, output);
                        System.out.println(
                                "Saved authorization information to \"" + output.getCanonicalPath() + "\".");

                        var responseHeaders = exchange.getResponseHeaders();
                        responseHeaders.set("Location", "http://localhost:8000/success");
                        exchange.sendResponseHeaders(302, 0);

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