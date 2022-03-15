module org.uludag.bmb{
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

    opens org.uludag.bmb.gui to javafx.fxml, javafx.controls, javafx.base, javafx.graphics;
    exports org.uludag.bmb.gui;
}
