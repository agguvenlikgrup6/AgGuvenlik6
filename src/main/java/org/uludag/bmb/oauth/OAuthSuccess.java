package org.uludag.bmb.oauth;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class OAuthSuccess implements HttpHandler {
    private final CountDownLatch latch;

    public OAuthSuccess(CountDownLatch latch) {
        this.latch = latch;
    }

    public void handle(HttpExchange t) throws IOException, IOException {
        String response = "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <title>Giriş Başarılı</title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <h2>Giriş Başarılı, Sayfayı Kapatıp Uygulamaya Dönebilirsiniz!</h2>\n" +
                "</body>\n" +
                "\n" +
                "</html>";
        t.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        t.sendResponseHeaders(200, bytes.length);
        OutputStream os = t.getResponseBody();
        os.write(bytes);
        os.close();

        latch.countDown();
    }
}
