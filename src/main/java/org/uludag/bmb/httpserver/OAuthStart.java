package org.uludag.bmb.httpserver;

import java.io.IOException;
import java.net.URL;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxPKCEWebAuth;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.TokenAccessType;
import com.dropbox.core.json.JsonReader;

import org.springframework.mock.web.MockHttpServletRequest;
import org.uludag.bmb.PropertiesReader;

public class OAuthStart {
    public void startOauthFlow() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("localhost:8000");
        
        URL argAppInfoFile = OAuthStart.class.getResource("/app.json");

        DbxAppInfo appInfo;
        try {
            assert argAppInfoFile != null;
            appInfo = DbxAppInfo.Reader.readFromFile(argAppInfoFile.getPath());
        } catch (JsonReader.FileLoadException ex) {
            System.err.println("Error reading <app-info-file>: " + ex.getMessage());
            System.exit(1);
            return;
        }
        
        HttpSession session = request.getSession(true);
        String state = "test-state";
        DbxSessionStore sessionStore = new DbxStandardSessionStore(session, state);
        String redirectUri = PropertiesReader.getProperty("redirectUri");

        DbxRequestConfig requestConfig = new DbxRequestConfig(PropertiesReader.getProperty("clientIdentifier"));
        DbxAppInfo appInfoWithoutSecret = new DbxAppInfo(appInfo.getKey());

        DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder()
                .withRedirectUri(redirectUri, sessionStore)
                .withTokenAccessType(TokenAccessType.OFFLINE)
                .withForceReapprove(false)
                // .withState(state)
                .build();

        DbxPKCEWebAuth pkceWebAuth = new DbxPKCEWebAuth(requestConfig, appInfoWithoutSecret);

        String authorizeUrl = pkceWebAuth.authorize(webAuthRequest);

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(authorizeUrl));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }


    }
}
