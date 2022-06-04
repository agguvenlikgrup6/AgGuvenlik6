@SuppressWarnings("all") open module org.uludag.bmb {
   requires transitive javafx.controls;
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
   requires org.bouncycastle.provider;
   requires com.microsoft.sqlserver.jdbc;

   exports org.uludag.bmb.beans.config;
   exports org.uludag.bmb.beans.constants;
   exports org.uludag.bmb.beans.crypto;
   exports org.uludag.bmb.beans.database;
   exports org.uludag.bmb.beans.database.query;
   exports org.uludag.bmb.beans.database.sharing;

   exports org.uludag.bmb.controller.config;
   exports org.uludag.bmb.controller.database;
   exports org.uludag.bmb.controller.scene;
   exports org.uludag.bmb.factory.query;

   exports org.uludag.bmb.oauth;

   exports org.uludag.bmb.operations;
   exports org.uludag.bmb.operations.database;
   exports org.uludag.bmb.operations.dropbox;
   exports org.uludag.bmb.operations.scenedatasource;

   exports org.uludag.bmb.service.cryption;
   exports org.uludag.bmb.service.sync;

}
