package org.uludag.bmb.operations;

import org.uludag.bmb.beans.dropbox.DbClient;

public class DbxOperations {
    protected DbClient client;

    public DbxOperations() {
        this.client = new DbClient(true);
    }
}
