package org.uludag.bmb.gui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

import org.uludag.bmb.controller.gui.StartupSceneController;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        new StartupSceneController().launchLoginScene(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}