package org.uludag.bmb.httpserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.dropbox.core.util.IOUtil;
import org.eclipse.jetty.server.Server;

public class AuthSuccess extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = new PrintWriter(IOUtil.utf8Writer(response.getOutputStream()));
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        out.println("<html>");
        out.println("<head><title>" + "Dropbox Auth" + "</title></head>");
        out.println("<body>");
        out.println("<h>Giriş İşlemi Başarılı, Sayfayı Kapatabilirsiniz!</h>");
        out.println("</body>");
        out.println("</html>");
        out.flush();

        URL url = new URL("http://localhost:8000/shutdown?token=true");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.getResponseCode();
    }
}
