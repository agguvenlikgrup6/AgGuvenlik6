package org.uludag.bmb.entity.dropbox;

import java.io.IOException;

import com.dropbox.core.v2.DbxClientV2;

public class DbClient {
    private DbxClientV2 client;
    private final DbAccount account;

    public DbClient(DbAccount account) {
        this.account = account;
        login();
    }

    public void login() {
        try {
            this.client = new DbxClientV2(account.getRequestConfig(), account.getCredential());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DbxClientV2 getClient() {
        return this.client;
    }

    // public void downloadFile(){

    // }
}
