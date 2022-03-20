package org.uludag.bmb.gui;

import java.io.IOException;
import java.net.URISyntaxException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TestSceneController {

    @FXML
    private Label lbl1;

    @FXML
    private Font x1;

    @FXML
    private Color x2;

    @FXML
    private Font x3;

    @FXML
    private Color x4;

    @FXML
    void loginToDropbox(ActionEvent event) throws IOException, URISyntaxException {
        lbl1.setText("value");
    }

}
