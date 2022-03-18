package org.uludag.bmb.oauth;

// import org.eclipse.jetty.server.Connector;
// import org.eclipse.jetty.server.Server;
// import org.eclipse.jetty.server.ServerConnector;
// import org.eclipse.jetty.server.handler.HandlerCollection;
// import org.eclipse.jetty.server.handler.ShutdownHandler;
// import org.eclipse.jetty.servlet.ServletContextHandler; 
import org.uludag.bmb.PropertiesReader;
 
public class OAuthHttpServer {

    // private Server server;
 
    // public void start() throws Exception {
    //     server = new Server();
    //     ServerConnector connector = new ServerConnector(server);
    //     connector.setPort(Integer.parseInt(PropertiesReader.getProperty("port")));
    //     server.setConnectors(new Connector[] { connector });
 
    //     ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);  
    //     context.setServer(server);
    //     context.addServlet(OAuthFlow.class, "/oauth");
    //     context.addServlet(OAuthSuccess.class, "/success"); 
        
    //     ShutdownHandler shutdownHandler = new ShutdownHandler("true");

    //     HandlerCollection collection = new HandlerCollection();
    //     collection.addHandler(context);
    //     collection.addHandler(shutdownHandler);
    //     server.setHandler(collection);

    //     server.start();
    // }
}