module org.uludag.bmb.gui{
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;
    requires dropbox.core.sdk;
    requires java.net.http;
    requires javafx.web;
    requires transitive javafx.graphics;
    requires jdk.httpserver;
    requires javaee.api;
    requires jetty.servlet.api;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.http;
    requires org.eclipse.jetty.io;
    requires org.eclipse.jetty.util;

    opens org.uludag.bmb.gui to javafx.fxml, javafx.controls, javafx.base, javafx.graphics;
    exports org.uludag.bmb.gui;
}
