package org.uludag.bmb.httpserver;

import java.io.IOException;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException; 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedirectHandler extends AbstractHandler { 
    private AuthCommand dropboxAuth;
  
    public RedirectHandler() {
        dropboxAuth = new AuthCommand();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if(target.equals("/oauth")) {
            dropboxAuth.doFinish(request, response);
        } else{
            response.sendError(400);
        }
    }
}