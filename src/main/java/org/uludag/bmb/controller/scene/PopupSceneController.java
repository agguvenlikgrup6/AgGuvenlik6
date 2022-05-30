package org.uludag.bmb.controller.scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PopupSceneController {
    protected Parent root;
    protected Stage stage;
    protected Scene scene;

    public PopupSceneController(String sceneFXML, String sceneTitle) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(PopupSceneController.class.getResource("/" + sceneFXML + ".fxml"));
            fxmlLoader.setController(this);
            root = (Parent) fxmlLoader.load();
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(sceneTitle);
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
