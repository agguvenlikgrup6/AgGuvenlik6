package org.uludag.bmb.controller.scene;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.uludag.bmb.beans.dataproperty.CustomComboBoxListener;
import org.uludag.bmb.beans.dataproperty.CustomTableView;
import org.uludag.bmb.operations.dropbox.FileOperations;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class ShareWindowController extends PopupSceneController implements Initializable {
    public ShareWindowController(MainSceneController mainSceneController, String sceneFXML, String sceneTitle) {
        super(mainSceneController, sceneFXML, sceneTitle);
    }

    @FXML
    private ComboBox<String> accountField;

    @FXML
    private Button btnAddShareAccount;

    @FXML
    private Button btnShareWithMails;

    @FXML
    private ListView<String> shareAccountList;

    @FXML
    private Label pathInfo;

    @FXML
    private ListView<String> shareFileList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> emailList = publicInfoOperations.getApplicationUsersList();
        for (String email : emailList) {
            accountField.getItems().addAll(email);
        }
        new CustomComboBoxListener<>(accountField);
        for (CustomTableView file : mainSceneController.fileListView.getSelectionModel().getSelectedItems()) {
            shareFileList.getItems().add(file.getFileName());
        }
    }

    @FXML
    void shareAccountRemove(ActionEvent event) {
        shareAccountList.getItems().remove(shareAccountList.getSelectionModel().getSelectedIndex());
    }

    @FXML
    void shareWithMails(ActionEvent event) {
        FileOperations.shareFile(mainSceneController.fileListView.getSelectionModel().getSelectedItems(), shareAccountList.getItems());
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void addShareAccount(ActionEvent event) {
        shareAccountList.getItems().add(accountField.getSelectionModel().getSelectedItem());
    }
}