module org.uludag.bmb{
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;
    requires dropbox.core.sdk;
    requires javafx.web;
    requires transitive javafx.graphics;
    requires transitive org.eclipse.jetty.server;
    requires org.eclipse.jetty.servlet;
    requires spring.test;    
    opens org.uludag.bmb.gui to javafx.fxml, javafx.controls, javafx.base, javafx.graphics;
    exports org.uludag.bmb.gui;
    exports org.uludag.bmb.oauth;
}
