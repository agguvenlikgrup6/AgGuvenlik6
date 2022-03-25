package org.uludag.bmb.controller.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.uludag.bmb.gui.App;
import org.uludag.bmb.oauth.OAuthFlow;

import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

public class StartupSceneController implements Initializable {

    @FXML
    private Button btnChoosePath;

    @FXML
    private Button btnLogin;

    @FXML
    private TextField chosenPath;

    @FXML
    private ProgressIndicator dbState;

    @FXML
    void chooseLocalPath(MouseEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Dropbox Depolama Dizinini Seçiniz");
        directoryChooser.setInitialDirectory(null);

        try {
            var path = directoryChooser.showDialog(null).getAbsolutePath();
            chosenPath.setText(path);
        } catch (NullPointerException ex) {
            chooseLocalPath(event);
        }

    }

    @FXML
    void finishStartup(MouseEvent event) {
        var path = chosenPath.getText();

        if (!new File(path).exists()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("HATA");
            alert.setHeaderText("Geçersiz Bir Dizin Seçtiniz");
            alert.setContentText("Lütfen Geçerli Bir Dizin Seçiniz");
            alert.showAndWait();
        } else {
            try {
                App.setRoot("mainScene");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void startOAuth(MouseEvent event) throws IOException {
        OAuthFlow oauth = new OAuthFlow();
        if (oauth.authValidation.isValid()) {
            dbState.setProgress(100.0D);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
