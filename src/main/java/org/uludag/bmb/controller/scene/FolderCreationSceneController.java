package org.uludag.bmb.controller.scene;

import java.net.URL;
import java.util.ResourceBundle;

import com.dropbox.core.DbxException;

import org.uludag.bmb.beans.dataproperty.StyledHyperLink;
import org.uludag.bmb.operations.dropbox.Client;
import org.uludag.bmb.operations.scenedatasource.UITrees;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;

public class FolderCreationSceneController extends PopupSceneController implements Initializable{
    @FXML
    private Button btnCreateNewFolder;

    @FXML
    private TextField folderName;

    @FXML
    private Label warningLabel;

    private String newFolderPath;

    @FXML
    void createNewFolder(ActionEvent event) {
        String newFolderName = folderName.getText();
        if (newFolderName != "") {
            try {
                Client.client.files().createFolderV2(newFolderPath + newFolderName);
                TreeItem<String> root = UITrees.Hierarchy.getAsTreeItem("");
                mainSceneController.directoriesHierarchyView.setRoot(root);
                mainSceneController.directoriesHierarchyView.setShowRoot(false);
                notificationOperations.insertNotification(newFolderPath + " dizininde " + newFolderName + " klasörü başarı ile oluşturuldu!");
            } catch (DbxException e) {
                notificationOperations.insertNotification(newFolderPath + " dizininde " + newFolderName + " klasörü oluşturulamadı!");
                e.printStackTrace();
            }
            this.stage.close();
        } else {
            warningLabel.setText("Klasör İsmi Boş Olamaz!");
        }
    }

    public FolderCreationSceneController(MainSceneController mainSceneController, String sceneFXML, String sceneTitle) {
        super(mainSceneController, sceneFXML, sceneTitle);
        stage.setResizable(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        newFolderPath = "/";
        var folderPathNode = mainSceneController.selectedDirectoryPathPane.getItems();
        if (folderPathNode.size() != 0) {
            for (int index = 1; index < folderPathNode.size(); index++) {
                newFolderPath += ((StyledHyperLink) mainSceneController.selectedDirectoryPathPane.getItems().get(index)).getText().toString();
            }
        }
    }
}
