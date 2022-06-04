package org.uludag.bmb.controller.scene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.util.Base64;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.config.LocalConfig;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.oauth.OAuthFlow;
import org.uludag.bmb.operations.database.TableOperations;
import org.uludag.bmb.operations.dropbox.DropboxClient;
import org.uludag.bmb.service.cryption.Crypto;
import org.uludag.bmb.service.sync.SyncControl;
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
                new SyncControl();

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
                KeyPair keyPair = Crypto.SHARE.CreateRSAKeyPair();
                String eMail = DropboxClient.client.users().getCurrentAccount().getEmail();

                String dataDir = System.getProperty("user.dir");
                String cacheSharedFileDir = dataDir;
                String cacheRecievedFileDir = dataDir;
                String os = System.getProperty("os.name").toLowerCase();

                if (os.indexOf("mac") >= 0) {
                    dataDir += "/Data/";
                    cacheSharedFileDir += "/Data/cache/sharedFiles/";
                    cacheRecievedFileDir += "/Data/cache/recievedFiles/";
                } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
                    dataDir += "/Data/";
                    cacheSharedFileDir += "/Data/cache/sharedFiles/";
                    cacheRecievedFileDir += "/Data/cache/recievedFiles/";
                } else {
                    dataDir += "\\Data\\";
                    cacheSharedFileDir += "\\Data\\cache\\sharedFiles\\";
                    cacheRecievedFileDir += "\\Data\\cache\\recievedFiles\\";
                }
                if (!Files.exists(Paths.get(dataDir))) {
                    File fileFolder = new File(dataDir);
                    fileFolder.mkdirs();
                }
                if (!Files.exists(Paths.get(cacheSharedFileDir))) {
                    File fileFolder = new File(cacheSharedFileDir);
                    fileFolder.mkdirs();
                }
                if (!Files.exists(Paths.get(cacheRecievedFileDir))) {
                    File fileFolder = new File(cacheRecievedFileDir);
                    fileFolder.mkdirs();
                }

                ConfigController.Settings.SaveSettings(new LocalConfig(chosenPath.getText(),
                        Base64.getUrlEncoder().encodeToString(keyPair.getPrivate().getEncoded()),
                        eMail, dataDir, cacheSharedFileDir, cacheRecievedFileDir));

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