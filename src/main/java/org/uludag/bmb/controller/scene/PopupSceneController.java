package org.uludag.bmb.controller.scene;

import org.uludag.bmb.beans.constants.Constants;
import org.uludag.bmb.operations.database.FileRecordOperations;
import org.uludag.bmb.operations.database.NotificationOperations;
import org.uludag.bmb.operations.database.SharingOperations;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PopupSceneController {
    protected Parent root;
    protected Stage stage;
    protected Scene scene;

    protected MainSceneController mainSceneController;

    protected NotificationOperations notificationOperations;
    protected SharingOperations publicInfoOperations;
    protected FileRecordOperations fileRecordOperations;

    public PopupSceneController(){
        notificationOperations = Constants.notificationOperations;
        publicInfoOperations = Constants.sharingOperations;
        fileRecordOperations = Constants.fileRecordOperations;
    }

    public PopupSceneController(MainSceneController mainSceneController, String sceneFXML, String sceneTitle) {
        this();
        this.mainSceneController = mainSceneController;
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
