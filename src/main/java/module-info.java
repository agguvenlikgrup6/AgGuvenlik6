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
    requires org.xerial.sqlitejdbc;
    requires java.sql;
    requires jdk.unsupported;
    requires org.apache.commons.io;
    requires java.net.http;
    requires commons.dbutils;
    requires commons.dbcp2;

    opens org.uludag.bmb.sync to java.net.http, commons.dbutils;
    opens org.uludag.bmb to javafx.fxml, javafx.controls, javafx.base, javafx.graphics, commons.dbutils;
    opens org.uludag.bmb.beans.dto to commons.dbutils;
    opens org.uludag.bmb.controller.scene to javafx.fxml, javafx.controls, javafx.base, javafx.graphics;
    opens org.uludag.bmb.controller.config to com.fasterxml.jackson.core, com.fasterxml.jackson.databind;
    opens org.uludag.bmb.beans.config to com.fasterxml.jackson.core, com.fasterxml.jackson.databind;
    exports org.uludag.bmb.beans.crypto to com.fasterxml.jackson.core, com.fasterxml.jackson.databind;
    opens org.uludag.bmb.beans.filedata to javafx.fxml, javafx.controls, javafx.base, javafx.graphics, org.uludag.bmb.controller.scene;
    exports org.uludag.bmb.controller.scene;
    exports org.uludag.bmb.oauth;
}
