package org.uludag.bmb.controller.scene;

import java.net.URL;
import java.util.ResourceBundle;

import org.uludag.bmb.operations.scenedatasource.UITrees;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class SelectShareFolderSceneController {

    @FXML
    private Button saveSharedToFolderBtn;

    @FXML
    private TreeView<String> selectShareFolderTreeView;

    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<String> root = UITrees.Hierarchy.getAsTreeItem("");
        selectShareFolderTreeView.setRoot((TreeItem<String>) root);
        selectShareFolderTreeView.setShowRoot(false);
    }
    @FXML
    void saveSharedToFolder(ActionEvent event) {

    }

}
