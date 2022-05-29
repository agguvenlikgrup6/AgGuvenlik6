package org.uludag.bmb.controller.scene;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.uludag.bmb.beans.dataproperty.AutoCompleteComboBoxListener;
import org.uludag.bmb.beans.dataproperty.TableViewDataProperty;
import org.uludag.bmb.controller.database.DatabaseController;
import org.uludag.bmb.operations.database.PublicInfoOperations;
import org.uludag.bmb.operations.dropbox.FileOperations;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;

public class ShareWindowController implements Initializable {
    private static final PublicInfoOperations publicInfoOperations = new PublicInfoOperations();

    private ObservableList<TableViewDataProperty> fileList;
    @FXML
    private ComboBox<String> accountField;

    @FXML
    private Button btnAddShareAccount;

    @FXML
    private Button btnShareWithMails;

    @FXML
    private ListView<String> shareAccountList;

    @FXML
    private ListView<String> shareFileList;

    @FXML
    private Label pathInfo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> emailList = publicInfoOperations.getUsersList();
        for (String email : emailList) {
            accountField.getItems().addAll(email);
        }
        new AutoCompleteComboBoxListener<>(accountField);
    }

    @FXML
    void shareAccountRemove(ActionEvent event) {
        shareAccountList.getItems().remove(shareAccountList.getSelectionModel().getSelectedIndex());
    }

    @FXML
    void shareWithMails(ActionEvent event) {
        FileOperations.SHARE_FILE(fileList, shareAccountList.getItems());
    }

    @FXML
    void addShareAccount(ActionEvent event) {
        shareAccountList.getItems().add(accountField.getSelectionModel().getSelectedItem());

    }

    public void setFileList(ObservableList<TableViewDataProperty> selectedFiles) {
        this.fileList = selectedFiles;
        for (TableViewDataProperty file : selectedFiles) {
            shareFileList.getItems().add( file.getFileName());
        }
    }

}