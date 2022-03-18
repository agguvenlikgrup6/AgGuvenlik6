package org.uludag.bmb.httpserver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.Desktop;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxPKCEWebAuth;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.TokenAccessType;
import com.dropbox.core.DbxWebAuth.BadRequestException;
import com.dropbox.core.DbxWebAuth.BadStateException;
import com.dropbox.core.DbxWebAuth.CsrfException;
import com.dropbox.core.DbxWebAuth.NotApprovedException;
import com.dropbox.core.DbxWebAuth.ProviderException;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.util.IOUtil;

import org.springframework.mock.web.MockHttpServletRequest;
import org.uludag.bmb.DbQueryParamOps;
import org.uludag.bmb.PropertiesReader;

public class OAuthFlow extends HttpServlet {

    private static DbxPKCEWebAuth pkceWebAuth;
    private static String redirectUri;
    private static DbxSessionStore sessionStore;
    private static String authorizeUrl;
    private static DbxAppInfo appInfo;
    String argAuthFileOutput = "authinfo.json";

    public OAuthFlow() {

    }

    public OAuthFlow(boolean X) throws IOException {
        if (X) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setServerName("localhost:8000");

            URL argAppInfoFile = OAuthStart.class.getResource("/app.json");

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
            sessionStore = new DbxStandardSessionStore(session, state);
            redirectUri = PropertiesReader.getProperty("redirectUri");

            DbxRequestConfig requestConfig = new DbxRequestConfig(PropertiesReader.getProperty("clientIdentifier"));
            DbxAppInfo appInfoWithoutSecret = new DbxAppInfo(appInfo.getKey());

            DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder()
                    .withRedirectUri(redirectUri, sessionStore)
                    .withTokenAccessType(TokenAccessType.OFFLINE)
                    .withForceReapprove(false)
                    .withState(state)
                    .build();

            pkceWebAuth = new DbxPKCEWebAuth(requestConfig, appInfoWithoutSecret);
            authorizeUrl = pkceWebAuth.authorize(webAuthRequest);

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI(authorizeUrl));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            DbxAuthFinish authFinish = pkceWebAuth.finishFromRedirect(redirectUri, sessionStore,
                    DbQueryParamOps.params("code",
                            DbQueryParamOps.extractQueryParam(request.getQueryString(), "code"), "state",
                            DbQueryParamOps.extractQueryParam(authorizeUrl, "state")));

            DbxCredential credential = new DbxCredential(authFinish.getAccessToken(), authFinish
                    .getExpiresAt(), authFinish.getRefreshToken(), appInfo.getKey(), appInfo.getSecret());

            File output = new File(argAuthFileOutput);
            try {
                DbxCredential.Writer.writeToFile(credential, output);
                System.out.println("Saved authorization information to \"" + output.getCanonicalPath() + "\".");
            } catch (IOException ex) {
                System.err.println("Error saving to <auth-file-out>: " + ex.getMessage());
                System.err.println("Dumping to stderr instead:");
                DbxCredential.Writer.writeToStream(credential, System.err);
                System.exit(1);
            }
        response.sendRedirect("http://localhost:8000/success");
        } catch (BadRequestException | BadStateException | CsrfException | NotApprovedException | ProviderException
                | DbxException e) {
            e.printStackTrace();
        }
    }
}
