package org.uludag.bmb.controller.scene;

import org.uludag.bmb.PropertiesReader;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class Controller {
    protected Parent parent;
    protected Stage stage;
    protected Scene scene;

    public Controller(String scene, int width, int heigth) {
        fxmlLoad(scene, width, heigth);
    }

    public void fxmlLoad(String fxml, int sceneWidth, int sceneHeight) {
        FXMLLoader fxmlLoader = new FXMLLoader(Controller.class.getResource("/" + fxml + ".fxml"));
        fxmlLoader.setController(this);
        try {
            parent = (Parent) fxmlLoader.load();
            scene = new Scene(parent);
            if(PropertiesReader.getProperty(fxml + "Css") != null){
                scene.getStylesheets().add(PropertiesReader.getProperty(fxml + "Css"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public abstract void displayScene(Stage stage);

}
