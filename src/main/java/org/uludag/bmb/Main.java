package org.uludag.bmb;

import org.uludag.bmb.oauth.OAuthFlow;

public class Main {
    public static void main(String[] args) throws Exception {
        OAuthFlow flow = new OAuthFlow();
        flow.startWithRedirect();

        org.uludag.bmb.gui.App.main(args);

    }

}
