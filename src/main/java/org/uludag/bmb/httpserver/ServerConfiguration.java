package org.uludag.bmb.httpserver;

import java.io.IOException;

import org.uludag.bmb.PropertiesReader;

public class ServerConfiguration {
    private String host;
    private int port;
    private String context;
    private String redirectUri;
    private int timeout;


    public String getHost() throws IOException {
        host = PropertiesReader.getProperty("host");
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() throws NumberFormatException, IOException {
        port = Integer.parseInt(PropertiesReader.getProperty("port"));
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContext() throws IOException {
        context = PropertiesReader.getProperty("context");
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getRedirectUri() throws IOException {
        redirectUri = PropertiesReader.getProperty("redirectUri");
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public int getTimeout() throws NumberFormatException, IOException {
        timeout = Integer.parseInt(PropertiesReader.getProperty("timeout"));
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    

}
