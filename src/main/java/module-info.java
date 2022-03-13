module org.uludag.bmb{
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;
    requires dropbox.core.sdk;
    requires javafx.web;
    requires json.simple;
    requires transitive javafx.graphics;

    opens org.uludag.bmb.gui to javafx.fxml, javafx.controls, javafx.base, javafx.graphics;
    exports org.uludag.bmb.gui;
}
