package org.uludag.bmb.httpserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthFinish extends HttpServlet {
    private AuthCommand authFinish;

    public AuthFinish() {
        authFinish = new AuthCommand();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        authFinish.doFinish(request, response);
    }
}
