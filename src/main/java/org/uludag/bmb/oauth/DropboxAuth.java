package org.uludag.bmb.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.registry.infomodel.User;

import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;

public class DropboxAuth {
    private final Common common;

    public DropboxAuth(Common common) {
        this.common = common;
    }

    public void doStart(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (!common.checkPost(request, response)) return;
  
        DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder()
            .withRedirectUri(getRedirectUri(request), getSessionStore(request))
            .build();
        String authorizeUrl = getWebAuth(request).authorize(authRequest);

        // Redirect the user to the Dropbox website so they can approve our application.
        // The Dropbox website will send them back to /dropbox-auth-finish when they're done.
        response.sendRedirect(authorizeUrl);
    }

    private DbxSessionStore getSessionStore(final HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String sessionKey = "dropbox-auth-csrf-token";
        return new DbxStandardSessionStore(session, sessionKey);
    }

    private DbxWebAuth getWebAuth(final HttpServletRequest request) {
        return new DbxWebAuth(common.getRequestConfig(request), common.dbxAppInfo);
    }

    private String getRedirectUri(final HttpServletRequest request) {
        return common.getUrl(request, "/dropbox-auth-finish");
    }
}
