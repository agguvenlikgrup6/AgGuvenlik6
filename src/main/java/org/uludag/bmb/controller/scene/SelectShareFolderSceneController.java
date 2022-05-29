package org.uludag.bmb.controller.scene;

import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import com.dropbox.core.DbxException;
import com.dropbox.core.util.StringUtil;
import com.dropbox.core.v2.sharing.SharedFileMetadata;

import org.uludag.bmb.beans.dataproperty.TableViewDataProperty;
import org.uludag.bmb.operations.dropbox.Client;
import org.uludag.bmb.operations.scenedatasource.UITrees;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;

public class SelectShareFolderSceneController implements Initializable {

    @FXML
    private Button saveSharedToFolderBtn;

    @FXML
    private TreeView<String> selectShareFolderTreeView;

    @FXML
    private TextField cloudPathTXT;

    private String fileName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<String> root = UITrees.Hierarchy.getAsTreeItem("");
        selectShareFolderTreeView.setRoot((TreeItem<String>) root);
        selectShareFolderTreeView.setShowRoot(false);
    }

    public void setFileList(String fileName) {
        this.fileName = fileName;
    }

    private void setFiles(ObservableList<String> fileList) {

    }

    @FXML
    void saveSharedToFolder(ActionEvent event) {
        System.out.println(123);
        List<SharedFileMetadata> entries;
        try {
            entries = Client.client.sharing().listReceivedFiles().getEntries();
            for (SharedFileMetadata entry : entries) {
                if (entry.getName().equals(fileName)) {
                    Client.client.files().saveUrl(cloudPathTXT.getText() + fileName, entry.getPreviewUrl());
                    
                }
            }

        } catch (DbxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("");
    }

    @FXML
    void getCloudPath(MouseEvent event) {
        ArrayList<String> path = new ArrayList<String>();
        ArrayList<String> pathNaked = new ArrayList<String>();

        TreeItem<String> selectedFolder = (TreeItem<String>) selectShareFolderTreeView.getSelectionModel()
                .getSelectedItem();
        var item = selectedFolder;

        if (item != null) {
            while (item.getParent() != null) {
                path.add(item.getValue() + "/");
                pathNaked.add(item.getValue() + "/");
                item = item.getParent();
            }
            Collections.reverse(path);
            Collections.reverse(pathNaked);
            String pathFolder = StringUtil.join(path, "");
            cloudPathTXT.setText(pathFolder);

            System.out.println(123);
        }
    }

}


