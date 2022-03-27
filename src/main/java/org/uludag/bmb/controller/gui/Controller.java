package org.uludag.bmb.controller.gui;

import java.io.IOException;

import com.dropbox.core.json.JsonReader.FileLoadException;

import org.uludag.bmb.entity.dropbox.DbClient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Controller {
    protected Parent parent;
    protected Stage stage;
    protected Scene scene;

    public void fxmlLoad(String fxml, int sceneWidth, int sceneHeight) {
        FXMLLoader fxmlLoader = new FXMLLoader(Controller.class.getResource("/" + fxml + ".fxml"));
        fxmlLoader.setController(this);
        try {
            parent = (Parent) fxmlLoader.load();
            scene = new Scene(parent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
