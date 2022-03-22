package org.uludag.bmb.gui;

import java.io.IOException;
import java.net.URISyntaxException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LoginSceneController {

    @FXML
    public TextField accesToken;

    @FXML
    private Button loginButton;

    public String accesTokenString;
    @FXML
    public void loginToDropbox(ActionEvent event) throws IOException, URISyntaxException {
       
         accesTokenString=accesToken.getText();
         System.out.println(accesTokenString);
        //  org.uludag.bmb.oauth.OAuthLogin.login(accesTokenString);
       
    }
    @FXML
    void takeToken(ActionEvent event) throws IOException, URISyntaxException {
        

        // org.uludag.bmb.oauth.OAuthLogin.takeTok();
        
        // lbl1.setText("value");
        // org.uludag.bmb.oauth.OAuthLogin.login();
    }
}
