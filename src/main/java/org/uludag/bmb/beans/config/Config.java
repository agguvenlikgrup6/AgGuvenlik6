package org.uludag.bmb.beans.config;


public class Config {
    private String localDropboxPath;
    private String privateRsaKey;
    private String userEmail;

    public Config(String path, String privateRsaKey, String userEmail) {
        this.localDropboxPath = path;
        this.privateRsaKey = privateRsaKey;
        this.userEmail = userEmail;
    }

    public Config(){

    }
    
    public String getLocalDropboxPath(){
        return this.localDropboxPath;
    }

    public String getPrivateRsaKey(){
        return this.privateRsaKey;
    }

    public String getUserEmail(){
        return this.userEmail;
    }
}
