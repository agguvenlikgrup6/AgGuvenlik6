package org.uludag.bmb.operations.dropbox;

import com.google.inject.AbstractModule;

public class Module extends AbstractModule{
    
    @Override
    protected void configure() {
        bind(Client.class);
    }
}
