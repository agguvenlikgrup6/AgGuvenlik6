package org.uludag.bmb.controller.scene;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import com.dropbox.core.DbxException;
import com.dropbox.core.util.StringUtil;
import com.dropbox.core.v2.sharing.SharedFileMetadata;

import org.uludag.bmb.operations.dropbox.DropboxClient;
import org.uludag.bmb.operations.scenedatasource.UITrees;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;

public class SaveSharedFileSceneController extends PopupSceneController implements Initializable{
    public SaveSharedFileSceneController(MainSceneController mainSceneController, String sceneFXML, String sceneTitle) {
        super(mainSceneController, sceneFXML, sceneTitle);
    }

    @FXML
    private Button saveSharedToFolderBtn;

    @FXML
    private TreeView<String> selectShareFolderTreeView;

    @FXML
    private TextField cloudPathTXT;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<String> root = UITrees.Hierarchy.getAsTreeItem("");
        selectShareFolderTreeView.setRoot((TreeItem<String>) root);
        selectShareFolderTreeView.setShowRoot(false);
    }

    @FXML
    void saveSharedFileToLocal(ActionEvent event) {
        List<SharedFileMetadata> entries;
        try {
            entries = DropboxClient.client.sharing().listReceivedFiles().getEntries();
            for (SharedFileMetadata entry : entries) {
                if (entry.getName().equals(mainSceneController.recievedFilesList.getSelectionModel().getSelectedItem().getEncryptedName())) {
                    // Client.client.files().saveUrl(cloudPathTXT.getText() + fileName, entry.getPreviewUrl());
                    // dosya önce ilgili dropbox dizinine kayıt edilecek
                    // dosyanın tablo kaydı oluşturulacak
                    // ardından dosyanın şifresi çözülecek ve ilgili local dizine atılacak
                    
                }
            }

            mainSceneController.lblSenderEmail.visibleProperty().set(false);
            mainSceneController.lblSender.visibleProperty().set(false);

        } catch (DbxException e) {
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
        }
    }

    @FXML
    void saveSharedToFolder(ActionEvent event){
        
    }
}
