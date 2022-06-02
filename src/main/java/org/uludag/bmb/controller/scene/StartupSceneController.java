package org.uludag.bmb.controller.scene;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Base64;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.localconfig.LocalConfig;
import org.uludag.bmb.controller.localconfig.LocalConfigController;
import org.uludag.bmb.oauth.OAuthFlow;
import org.uludag.bmb.operations.database.TableOperations;
import org.uludag.bmb.operations.dropbox.DropboxClient;
import org.uludag.bmb.service.cryption.Crypto;
import org.uludag.bmb.service.sync.SyncMonitor;

import com.dropbox.core.json.JsonReader.FileLoadException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class StartupSceneController extends SceneController {

    @FXML
    private Button btnChoosePath;

    @FXML
    private Button btnLogin;

    @FXML
    private TextField chosenPath;

    @FXML
    private ProgressIndicator dbState;

    public StartupSceneController() throws FileLoadException {
        super(PropertiesReader.getProperty("startupSceneFxml"),
                Integer.parseInt(PropertiesReader.getProperty("startupSceneWidth")),
                Integer.parseInt(PropertiesReader.getProperty("startupSceneHeigth")));
    }

    @Override
    public void displayScene(Stage stage) {
        TableOperations tableOperations = new TableOperations();

        tableOperations.createLocalTables();
        try {
            if (DropboxClient.client != null) {
                new Thread(new SyncMonitor()).start();

                MainSceneController msc = new MainSceneController();
                msc.displayScene(stage);
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
        } catch (NullPointerException ex) {
            chooseLocalPath(event);
        }
    }

    @FXML
    void finishStartup(MouseEvent event) {
        try {
            var path = chosenPath.getText();

            if (!new File(path).exists()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("HATA");
                alert.setHeaderText("Geçersiz Bir Dizin Seçtiniz");
                alert.setContentText("Lütfen Geçerli Bir Dizin Seçiniz");
                alert.showAndWait();
            } else {
                tableOperations.createLocalTables();

                if (DropboxClient.client == null) {
                    DropboxClient.client = DropboxClient.getClient();
                }
                KeyPair keyPair = Crypto.SHARE.CREATE_KEY_PAIR();
                String eMail = DropboxClient.client.users().getCurrentAccount().getEmail();
                LocalConfigController.Settings.SaveSettings(new LocalConfig(chosenPath.getText(),
                        Base64.getUrlEncoder().encodeToString(keyPair.getPrivate().getEncoded()),
                        eMail));

                userInformationOperations.insert(eMail,
                        Base64.getUrlEncoder().encodeToString(keyPair.getPublic().getEncoded()));
                new Thread(new SyncMonitor()).start();
                new MainSceneController().displayScene(stage);
            }

        } catch (Exception e) {

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