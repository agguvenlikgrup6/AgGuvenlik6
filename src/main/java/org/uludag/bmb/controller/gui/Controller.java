package org.uludag.bmb.controller.gui;

import java.io.IOException;

import org.uludag.bmb.entity.dropbox.DbAccount;
import org.uludag.bmb.entity.dropbox.DbClient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Controller {
    public DbClient client;
    public DbAccount account;

    protected Parent parent;
    protected Stage stage;
    protected Scene scene;

    public Controller() {
        this.account = new DbAccount();
        this.client = new DbClient(account);
    }

    public void fxmlLoad(String fxml, int sceneWidth, int sceneHeight){
        FXMLLoader fxmlLoader = new FXMLLoader(Controller.class.getResource("/" + fxml + ".fxml"));
        fxmlLoader.setController(this);
        try{
            parent = (Parent) fxmlLoader.load();
            scene = new Scene(parent, sceneWidth, sceneHeight);
        } catch (IOException ex){
            // TODO handle exception
            System.err.println(ex.getMessage());
        }
    }

}
