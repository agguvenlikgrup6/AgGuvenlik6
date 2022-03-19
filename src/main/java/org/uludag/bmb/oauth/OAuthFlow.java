package org.uludag.bmb.oauth;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

    public void startFlow() throws IOException {
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

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(authorizeUrl));

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
                                params("code", extractQueryParam(redirectQuery, "code"),
                                        "state", extractQueryParam(redirectQuery, "state")));

                        DbxCredential credential = new DbxCredential(authFinish.getAccessToken(), authFinish
                                .getExpiresAt(), authFinish.getRefreshToken(), appInfo.getKey(), appInfo.getSecret());

                        File output = new File("authinfo.json");
                        DbxCredential.Writer.writeToFile(credential, output);
                        System.out.println("Saved authorization information to \"" + output.getCanonicalPath() + "\".");

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

    protected String extractQueryParam(String query, String param) {
        Map<String, List<String>> params = toParamsMap(query);

        if (!params.containsKey(param)) {
            return null;
        }

        List<String> values = params.get(param);
        if (values.size() > 1) {
            return null;
        }

        return values.get(0);
    }

    protected Map<String, List<String>> toParamsMap(String query) {
        try {
            String[] pairs = query.split("&");
            Map<String, List<String>> params = new HashMap<String, List<String>>(pairs.length);

            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                String key = keyValue[0];
                String value = keyValue.length == 2 ? keyValue[1] : "";

                List<String> others = params.get(key);
                if (others == null) {
                    others = new ArrayList<String>();
                    params.put(key, others);
                }

                others.add(URLDecoder.decode(value, "UTF-8"));
            }

            return params;
        } catch (Exception ex) {
            return null;
        }
    }

    protected Map<String, String[]> params(String... pairs) {
        if ((pairs.length % 2) != 0) {
            System.out.println("pairs must be a multiple of 2.");
        }

        Map<String, String[]> query = new HashMap<String, String[]>();
        for (int i = 0; i < pairs.length; i += 2) {
            query.put(pairs[i], new String[] { pairs[i + 1] });
        }
        return query;
    }

}
