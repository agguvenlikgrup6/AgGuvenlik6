package org.uludag.bmb.oauth;

import com.dropbox.core.*;

import org.uludag.bmb.PropertiesReader;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Pkce {
    public final HttpServletRequest request;

    public Pkce(HttpServletRequest request) {
        this.request = request;
    }

    public DbxAuthFinish authorize(DbxAppInfo appInfo) throws IOException {
        HttpSession session = request.getSession(true);
        String state = "dropbox-auth-csrf-token";
        DbxSessionStore sessionStore = new DbxStandardSessionStore(session, state);
        String redirectUri = PropertiesReader.getProperty("redirectUri");

        DbxRequestConfig requestConfig = new DbxRequestConfig(PropertiesReader.getProperty("clientIdentifier"));
        DbxAppInfo appInfoWithoutSecret = new DbxAppInfo(appInfo.getKey());

        DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder()
                .withRedirectUri(redirectUri, sessionStore)
                // .withTokenAccessType(TokenAccessType.OFFLINE)
                // .withForceReapprove(false)
                .withState(state)
                .build();

        DbxPKCEWebAuth pkceWebAuth = new DbxPKCEWebAuth(requestConfig, appInfoWithoutSecret);

        String authorizeUrl = pkceWebAuth.authorize(webAuthRequest);
        System.out.println("LINK: " + authorizeUrl);

        try {
            return pkceWebAuth.finishFromRedirect(redirectUri, sessionStore,SimpleSessionStore.params("state", SimpleSessionStore.extractQueryParam(authorizeUrl, "state")));
        } catch (DbxException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } catch (DbxWebAuth.ProviderException e) {
            e.printStackTrace();
        } catch (DbxWebAuth.NotApprovedException e) {
            e.printStackTrace();
        } catch (DbxWebAuth.BadRequestException e) {
            e.printStackTrace();
        } catch (DbxWebAuth.BadStateException e) {
            e.printStackTrace();
        } catch (DbxWebAuth.CsrfException e) {
            e.printStackTrace();
        }
        return null;
    }
}
