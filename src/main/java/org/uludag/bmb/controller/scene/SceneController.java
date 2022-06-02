package org.uludag.bmb.controller.scene;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.constants.Constants;
import org.uludag.bmb.operations.database.FileRecordOperations;
import org.uludag.bmb.operations.database.NotificationOperations;
import org.uludag.bmb.operations.database.TableOperations;
import org.uludag.bmb.operations.database.UserInformationOperations;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class SceneController {
    protected NotificationOperations notificationOperations;
    protected FileRecordOperations fileRecordOperations;
    protected UserInformationOperations userInformationOperations;
    protected TableOperations tableOperations;

    protected Parent parent;
    protected Stage stage;
    protected Scene scene;

    public SceneController() {

    }

    public SceneController(String scene, int width, int heigth) {
        fxmlLoad(scene, width, heigth);
        notificationOperations = Constants.notificationOperations;
        fileRecordOperations = Constants.fileRecordOperations;
        userInformationOperations = Constants.userInformationOperations;
        tableOperations = Constants.tableOperations;
    }

    public void fxmlLoad(String fxml, int sceneWidth, int sceneHeight) {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneController.class.getResource("/" + fxml + ".fxml"));
        fxmlLoader.setController(this);
        try {
            parent = (Parent) fxmlLoader.load();
            scene = new Scene(parent);
            if (PropertiesReader.getProperty(fxml + "Css") != null) {
                scene.getStylesheets().add(PropertiesReader.getProperty(fxml + "Css"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public abstract void displayScene(Stage stage);
}
