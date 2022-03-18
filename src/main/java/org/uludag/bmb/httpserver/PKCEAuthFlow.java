package org.uludag.bmb.httpserver;

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
import com.dropbox.core.util.IOUtil;

import org.springframework.mock.web.MockHttpServletRequest;
import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.oauth.SimpleSessionStore;

public class PKCEAuthFlow extends HttpServlet {

    private static DbxPKCEWebAuth pkceWebAuth;
    private static String redirectUri;
    private static DbxSessionStore sessionStore;
    private static String authorizeUrl;

    public PKCEAuthFlow() {

    }

    public PKCEAuthFlow(boolean X) throws IOException {
        if (X) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setServerName("localhost:8000");

            URL argAppInfoFile = AuthStart.class.getResource("/app.json");

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
            DbxAuthFinish s = pkceWebAuth.finishFromRedirect(redirectUri, sessionStore,
                    SimpleSessionStore.params("code",
                            SimpleSessionStore.extractQueryParam(request.getQueryString(), "code"), "state",
                            SimpleSessionStore.extractQueryParam(authorizeUrl, "state")));
            String dropboxAccessToken = s.getAccessToken();
            System.out.println(dropboxAccessToken);
            
            PrintWriter out = new PrintWriter(IOUtil.utf8Writer(response.getOutputStream()));
            response.sendRedirect("http://localhost:8000/success");
            response.setContentType("text/html");
            response.setCharacterEncoding("utf-8");
            out.println("<html>");
            out.println("<head><title>" + "Dropbox Auth" + "</title></head>");
            out.println("<body>");
            out.println("<h>Giriş İşlemi Başarılı, Sayfayı Kapatabilirsiniz!</h>");
            out.println("</body>");
            out.println("</html>");
            out.flush();
        } catch (BadRequestException | BadStateException | CsrfException | NotApprovedException | ProviderException
                | DbxException e) {
            e.printStackTrace();
        }
    }
}
