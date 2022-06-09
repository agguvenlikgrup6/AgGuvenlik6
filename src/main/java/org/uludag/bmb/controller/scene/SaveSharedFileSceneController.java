package org.uludag.bmb.controller.scene;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.uludag.bmb.beans.constants.Constants;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.beans.database.sharing.RecievedFile;
import org.uludag.bmb.operations.dropbox.DropboxClient;
import org.uludag.bmb.operations.scenedatasource.UITrees;

import com.dropbox.core.DbxException;
import com.dropbox.core.util.StringUtil;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.sharing.SharedFileMetadata;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;

public class SaveSharedFileSceneController extends PopupSceneController implements Initializable {
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
    void saveSharedFileToCloud(ActionEvent event) {
        List<SharedFileMetadata> entries;
        try {
            var selectedRecievedFile = mainSceneController.recievedFilesList.getSelectionModel().getSelectedItem();
            if (selectedRecievedFile != null) {
                entries = DropboxClient.client.sharing().listReceivedFiles().getEntries();
                for (SharedFileMetadata entry : entries) {
                    if (entry.getName().equals(selectedRecievedFile.getEncryptedName())) {
                        ListFolderResult result = DropboxClient.files().listFolderBuilder(cloudPathTXT.getText()).start();
                        List<Metadata> folderEntries = result.getEntries();
                        for (Metadata folderEntry : folderEntries) {
                            if (folderEntry instanceof FileMetadata) {
                                FileMetadata fileEntry = (FileMetadata) folderEntry;
                                if (fileEntry.getName().equals(selectedRecievedFile.getDecryptedName()) && cloudPathTXT.getText().equals(fileEntry.getPathDisplay())) {
                                    notificationOperations.insert("Seçilen dizin içerisinde aynı isme sahip başka bir dosya bulunmakta. Lütfen başka bir dizin seçiniz!");
                                    return;
                                }
                            }
                        }

                        DropboxClient.files().saveUrl(cloudPathTXT.getText() + entry.getName(), entry.getPreviewUrl());
                        RecievedFile recievedFile = recievedFileOperations.getByEncryptedName(entry.getName());
                        fileRecordOperations.insert(new FileRecord(0, recievedFile.getDecryptedName(), cloudPathTXT.getText(), recievedFile.getFileKey(), recievedFile.getModificationDate(), recievedFile.getHash(), recievedFile.getEncryptedName(), 0, 0, recievedFile.getFileSize(), ""));

                        DropboxClient.sharing().relinquishFileMembership(entry.getId());

                        recievedFileOperations.deleteByEncryptedName(recievedFile.getEncryptedName());
                        notificationOperations.insert(recievedFile.getDecryptedName() + " dosyası başarı ile " + cloudPathTXT.getText() + " dizinine kaydedildi!");
                        mainSceneController.recievedFilesList.getItems().remove(selectedRecievedFile);
                    } else if (entry.getName().equals(Constants.ACCOUNT.userEmail + "+" + selectedRecievedFile.getEncryptedName())) {
                        DropboxClient.sharing().relinquishFileMembership(Constants.ACCOUNT.userEmail + "+" + selectedRecievedFile.getEncryptedName());
                    }
                }

            }

            mainSceneController.lblSenderEmail.visibleProperty().set(false);
            mainSceneController.lblSender.visibleProperty().set(false);

            this.stage.close();
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void getCloudPath(MouseEvent event) {
        ArrayList<String> path = new ArrayList<String>();
        ArrayList<String> pathNaked = new ArrayList<String>();

        TreeItem<String> selectedFolder = (TreeItem<String>) selectShareFolderTreeView.getSelectionModel().getSelectedItem();
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
    void saveSharedToFolder(ActionEvent event) {

    }
}
