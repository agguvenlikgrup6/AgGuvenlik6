package org.uludag.bmb;

import java.io.IOException;

import org.uludag.bmb.controller.scene.StartupSceneController;
import org.uludag.bmb.service.sync.SyncServer;

import com.dropbox.core.json.JsonReader.FileLoadException;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException, FileLoadException {
        new StartupSceneController().displayScene(stage);
    }

    @Override
    public void stop() throws Exception {
        SyncServer.stopSyncServer();
        System.exit(1);
    }

    public static void main(String[] args) {
        launch(args);
    }

}