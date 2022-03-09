module org.uludag.bmb.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;
    requires dropbox.core.sdk;
    requires transitive javafx.graphics;
    
    opens org.uludag.bmb.gui to javafx.fxml;
    exports org.uludag.bmb.gui;
}
