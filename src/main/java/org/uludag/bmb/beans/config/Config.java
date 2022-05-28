package org.uludag.bmb.beans.config;


public class Config {
    private String localDropboxPath;

    public Config(String path) {
        this.localDropboxPath = path;
    }

    public Config(){

    }
    
    public String getLocalDropboxPath(){
        return this.localDropboxPath;
    }
}
