package org.uludag.bmb.controller.scene;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import com.dropbox.core.DbxException;
import com.dropbox.core.json.JsonReader.FileLoadException;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.config.Config;
import org.uludag.bmb.controller.StartupControl;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.controller.database.DatabaseController;
import org.uludag.bmb.oauth.OAuthFlow;
import org.uludag.bmb.operations.database.PublicInfoOperations;
import org.uludag.bmb.operations.database.TableOperations;
import org.uludag.bmb.operations.dropbox.Client;
import org.uludag.bmb.service.cryption.Crypto;
import org.uludag.bmb.service.sync.SyncMonitor;

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

    public StartupSceneController() throws FileLoadException {
        super(PropertiesReader.getProperty("startupSceneFxml"),
                Integer.parseInt(PropertiesReader.getProperty("startupSceneWidth")),
                Integer.parseInt(PropertiesReader.getProperty("startupSceneHeigth")));
    }

    @Override
    public void displayScene(Stage stage) {
        try {
            if (Client.client != null) {
                StartupControl sc = new StartupControl();
                sc.deletedFileControl();
                sc.downloadFileControl();
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

            ConfigController.Settings.SaveSettings(new Config(path));

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
            TableOperations tableOperations = new TableOperations();

            tableOperations.createNotificationTable();
            tableOperations.createRecordTable();
            tableOperations.createSharedRecordTable();
            tableOperations.createPrivateKeyTable();

            PublicInfoOperations publicInfoOperations = new PublicInfoOperations();

            if (Client.client == null) {
                Client.client = Client.getClient();
            }

            KeyPair keyPair = Crypto.SHARE.CREATE_KEY_PAIR();
            publicInfoOperations.insertShareKeys(Base64.getUrlEncoder().encodeToString(keyPair.getPublic().getEncoded()),
                    Base64.getUrlEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
            new Thread(new SyncMonitor()).start();
            new MainSceneController().displayScene(stage);
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