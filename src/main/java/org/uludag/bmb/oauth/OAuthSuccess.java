package org.uludag.bmb.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class OAuthSuccess implements HttpHandler {
    private final CountDownLatch latch;

    public OAuthSuccess(CountDownLatch latch) {
        this.latch = latch;    
    }

    public void handle(HttpExchange t) throws IOException, IOException {
        InputStream is = t.getRequestBody();
        InputStreamReader isReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isReader);
        StringBuffer sb = new StringBuffer();
        String str;

        while((str = reader.readLine())!= null){
            sb.append(str);
        }

        String response = "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <title>Auth Success</title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <h2>Giriş Başarılı, Sayfayı Kapatabilirsiniz!</h2>\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();

        latch.countDown();
        latch.countDown();
    }
}
