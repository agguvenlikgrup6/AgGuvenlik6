package org.uludag.bmb.controller.gui;

import java.io.File;
import java.io.IOException;

import com.dropbox.core.json.JsonReader.FileLoadException;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.controller.dropbox.LocalDropbox;
import org.uludag.bmb.entity.dropbox.DbClient;
import org.uludag.bmb.oauth.OAuthFlow;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class StartupSceneController extends Controller {

    @FXML
    private Button btnChoosePath;

    @FXML
    private Button btnLogin;

    @FXML
    private TextField chosenPath;

    @FXML
    private ProgressIndicator dbState;

    public StartupSceneController() throws IOException, FileLoadException {
        try {
            fxmlLoad(PropertiesReader.getProperty("startupSceneFxml"),
                    Integer.parseInt(PropertiesReader.getProperty("startupSceneWidth")),
                    Integer.parseInt(PropertiesReader.getProperty("startupSceneHeigth")));
        } catch (NumberFormatException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public void displayLoginScene(Stage stage) {
        try {
            if (new DbClient().login()) {
                new MainSceneController().displayHomeScreen(stage);
            } else {
                this.stage = stage;
                stage.setScene(scene);
                stage.setResizable(false);
                stage.hide();
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void chooseLocalPath(MouseEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Dropbox Depolama Dizinini Seçiniz");
        directoryChooser.setInitialDirectory(null);

        try {
            String path = directoryChooser.showDialog(null).getAbsolutePath();
            chosenPath.setText(path);
            // LocalDropbox.WRITER.;
        } catch (NullPointerException ex) {
            chooseLocalPath(event);
        }

    }

    @FXML
    void finishStartup(MouseEvent event) throws IOException, FileLoadException {
        var path = chosenPath.getText();

        if (!new File(path).exists()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("HATA");
            alert.setHeaderText("Geçersiz Bir Dizin Seçtiniz");
            alert.setContentText("Lütfen Geçerli Bir Dizin Seçiniz");
            alert.showAndWait();
        } else {

            new MainSceneController().displayHomeScreen(stage);
        }
    }

    @FXML
    void startOAuth(MouseEvent event) throws IOException, FileLoadException {
        if (dbState.getProgress() < 100.0D) {
            OAuthFlow oauth = new OAuthFlow();
            oauth.startWithRedirect();

            dbState.setProgress(100.0D);
        }
    }
}
