package org.uludag.bmb.httpserver;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.uludag.bmb.PropertiesReader;

public class AuthHttpServer {

    private Server server;

    public void start() throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(Integer.parseInt(PropertiesReader.getProperty("port")));
        server.setConnectors(new Connector[] { connector });
 
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS); 
        context.setServer(server);
        context.addServlet(PKCEAuthFlow.class, "/oauth");
        context.addServlet(AuthSuccess.class, "/success"); 
        
        ShutdownHandler sd = new ShutdownHandler("true");

        HandlerCollection collection = new HandlerCollection();
        collection.addHandler(context);
        collection.addHandler(sd);
        server.setHandler(collection);
        server.start();
    }
}


