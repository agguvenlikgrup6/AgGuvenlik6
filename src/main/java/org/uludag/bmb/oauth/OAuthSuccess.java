package org.uludag.bmb.oauth;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class OAuthSuccess implements HttpHandler {

    public void handle(HttpExchange t) throws IOException, IOException {

    }

    // public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    //     PrintWriter out = new PrintWriter(IOUtil.utf8Writer(response.getOutputStream()));
    //     response.setContentType("text/html");
    //     response.setCharacterEncoding("utf-8");
    //     out.println("<html>");
    //     out.println("<head><title>" + "Dropbox Auth" + "</title></head>");
    //     out.println("<h>Giriş İşlemi Başarılı, Sayfayı Kapatabilirsiniz!</h>");
    //     out.println("</body>");
    //     out.println("</html>");
    //     out.flush();

    //     URL url = new URL("http://localhost:8000/shutdown?token=true");
    //     HttpURLConnection connection = (HttpURLConnection)url.openConnection();
    //     connection.setRequestMethod("POST");
    //     connection.getResponseCode();
    // }
}
