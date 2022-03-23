package org.uludag.bmb.controller;

import java.io.IOException;

import org.uludag.bmb.gui.App;
import org.uludag.bmb.oauth.OAuthFlow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.uludag.bmb.gui.App;

public class LoginSceneController {

    @FXML
    private VBox loginButon;

    @FXML
    private Button loginButton;

    @FXML
    void loginToDropbox(MouseEvent event) throws IOException {
        App.setRoot("mainScene");
        System.out.println(123);
    }

}