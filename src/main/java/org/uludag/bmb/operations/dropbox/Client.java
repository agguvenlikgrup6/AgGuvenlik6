package org.uludag.bmb.operations.dropbox;

import java.io.IOException;

import com.dropbox.core.v2.DbxClientV2;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Client {
    private DbxClientV2 client;
    private final Account account;

    @Inject
    public Client(Account account) {
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
}
