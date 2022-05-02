package org.uludag.bmb.sync;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sun.net.httpserver.HttpServer;

import org.uludag.bmb.PropertiesReader;

public class SyncStatus {
    private AtomicBoolean syncStatus = new AtomicBoolean(false);
    private HttpServer server;

    public SyncStatus() throws Exception {
        this.server = HttpServer
                .create(new InetSocketAddress(Integer.parseInt(PropertiesReader.getProperty("syncStatusPort"))), 0);
        server.createContext("/status", exchange -> {
            if (this.syncStatus.get()) {
                exchange.sendResponseHeaders(200, 0);
            } else {
                exchange.sendResponseHeaders(500, 0);
            }
        });
        server.createContext("/stop", t -> {
            this.server.stop(0);
        });
        server.setExecutor(null);
        server.start();
        this.syncStatus.set(true);
    }

    public void setStatus(boolean newStatus) {
        this.syncStatus.set(newStatus);
    }

    public static boolean getSyncStatus() {
        try {
            URL obj = new URL(PropertiesReader.getProperty("syncStatusURL"));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static void stopSyncServer() {
        try {
            URL obj = new URL(PropertiesReader.getProperty("syncStop"));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}