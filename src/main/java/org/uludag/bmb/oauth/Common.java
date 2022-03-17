package org.uludag.bmb.oauth;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.util.LangUtil;

public class Common {
    private static final String antiCsrfTokenName = "anti-csrf-token";
    public final DbxAppInfo dbxAppInfo;

    public Common(DbxAppInfo dbxAppInfo) {
        this.dbxAppInfo = dbxAppInfo;
    }


    public boolean checkPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        if(!request.getMethod().equals("POST")){
            page(response, 405);
            response.sendError(405);

            return false;
        }

        return true;
    }

    public void page(HttpServletResponse response, int statusCode){
        response.setStatus(statusCode);
    }

    public String checkAntiCsrfToken(HttpServletRequest request) throws IOException, ServletException
    {
        if (request.getContentType() != null &&
            request.getContentType().toLowerCase().indexOf("multipart/form-data") > -1 &&
            request.getPart("anti-csrf-token") == null ||
            request.getParameter("anti-csrf-token") == null) {
            return "missing \"" + antiCsrfTokenName + "\" POST parameter";
        }

        return null;
    }

    public DbxRequestConfig getRequestConfig(HttpServletRequest request) {
        return DbxRequestConfig.newBuilder("example-web-file-browser")
            .withUserLocaleFrom(request.getLocale())
            .build();
    }

    public String getUrl(HttpServletRequest request, String path) {
        URL requestUrl;
        try {
            requestUrl = new URL(request.getRequestURL().toString());
            return new URL(requestUrl, path).toExternalForm();
        } catch (MalformedURLException ex) {
            throw LangUtil.mkAssert("Bad URL", ex);
        }
    }
}
