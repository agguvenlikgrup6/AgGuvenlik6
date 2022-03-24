package org.uludag.bmb.operations.dropbox;

import com.google.inject.AbstractModule;

public class DbModule extends AbstractModule{
    
    @Override
    protected void configure() {
        bind(DbClient.class);
    }
}
