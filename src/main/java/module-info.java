module org.uludag.bmb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;
    requires transitive dropbox.core.sdk;
    requires javafx.web;
    requires transitive javafx.graphics;
    requires spring.test;
    requires transitive jdk.httpserver;
    requires transitive javax.servlet.api;
    requires com.google.guice;

    opens org.uludag.bmb.gui to javafx.fxml, javafx.controls, javafx.base, javafx.graphics;
    opens org.uludag.bmb.controller to javafx.fxml, javafx.controls, javafx.base, javafx.graphics;
    opens org.uludag.bmb.operations.dropbox to com.google.guice;

    exports org.uludag.bmb.gui;
    exports org.uludag.bmb.controller;
    exports org.uludag.bmb.oauth;
    exports org.uludag.bmb.operations.dropbox;
}
