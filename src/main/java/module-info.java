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
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;

    opens org.uludag.bmb to javafx.fxml, javafx.controls, javafx.base, javafx.graphics;
    opens org.uludag.bmb.controller.scene to javafx.fxml, javafx.controls, javafx.base, javafx.graphics;
    opens org.uludag.bmb.controller.config to com.fasterxml.jackson.core, com.fasterxml.jackson.databind;
    opens org.uludag.bmb.entity.config to com.fasterxml.jackson.core, com.fasterxml.jackson.databind;
    opens org.uludag.bmb.controller.scene.ListView to javafx.fxml, javafx.controls, javafx.base, javafx.graphics;

    exports org.uludag.bmb.controller.scene;
    exports org.uludag.bmb.oauth;
}
