package org.uludag.bmb;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.uludag.bmb.oauth.OAuthFlow;
import org.uludag.bmb.operations.dropbox.DbClient;
import org.uludag.bmb.operations.dropbox.DbModule;

public class Main {
    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new DbModule());
        DbClient client = injector.getInstance(DbClient.class);
        
        OAuthFlow flow = new OAuthFlow();
        flow.startWithRedirect();

        org.uludag.bmb.gui.App.main(args);
        org.uludag.bmb.operations.dropbox.DbTest.main(args);
    }

}
