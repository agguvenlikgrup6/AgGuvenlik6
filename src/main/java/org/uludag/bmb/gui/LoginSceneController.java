package org.uludag.bmb.gui;

import java.io.IOException;

import org.uludag.bmb.oauth.OAuthFlow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

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