package org.uludag.bmb.beans.localconfig;


public class LocalConfig {
    private String localDropboxPath;
    private String privateRsaKey;
    private String userEmail;

    public LocalConfig(String path, String privateRsaKey, String userEmail) {
        this.localDropboxPath = path;
        this.privateRsaKey = privateRsaKey;
        this.userEmail = userEmail;
    }

    public LocalConfig(){

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
