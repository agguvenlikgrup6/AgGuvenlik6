package org.uludag.bmb.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import org.uludag.bmb.entity.*;
import org.uludag.bmb.oauth.DbxClientLogin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class mainSceneController extends DbxClientLogin implements Initializable {
    String path;

    @FXML
    private Button btnDownload;

    @FXML
    private Button btnUpload;

    @FXML
    private TreeView treeView;

    @FXML
    private Text files;

    @FXML
    private TreeView showFiles;

    @FXML
    private Font x1;

    @FXML
    private Color x2;

    @FXML
    private Font x3;

    @FXML
    private Color x4;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MXMTree hierarchy = new DBHierarchy().getHierarchy();
        MXMNode hierarchyRoot = hierarchy.root;

        TreeItem<String> rootItem = new TreeItem<>(hierarchyRoot.data);

        treeView.setRoot(rootItem);

        addtotree(rootItem, hierarchyRoot);

    }

    private void addtotree(TreeItem<String> rootItem, MXMNode hierarchyRoot) {
        if (hierarchyRoot.childs.size() != 0) {
            int i = 0;
            for (MXMNode c : hierarchyRoot.childs) {
                hierarchyRoot = c;
                rootItem.getChildren().add(new TreeItem<>(hierarchyRoot.data));
                addtotree(rootItem.getChildren().get(i++), hierarchyRoot);
            }
        } else {
            for (MXMNode c : hierarchyRoot.leafs) {
                rootItem.getChildren().add(new TreeItem<>(c.data));
            }
        }
    }

    @FXML
    void downloadFile(MouseEvent event) {

    }

    @FXML
    void selectItem(MouseEvent event) {
        TreeItem<String> item = (TreeItem<String>) treeView.getSelectionModel().getSelectedItem();

        try {
            ArrayList<String> pathList = new ArrayList<>();
            path = "";
            var fake = item;

            if (item.getParent() == null) {
                path += "/";
            } else {

                while (fake.getParent() != null) {
                    pathList.add("/");
                    pathList.add(fake.getValue());
                    fake = fake.getParent();
                }

                pathList.add("/");
                Collections.reverse(pathList);
            }

            for (String p : pathList) {
                path += p;
            }

            ListFolderResult result = client.files().listFolder(path);

            List<Metadata> entries = result.getEntries();

            TreeItem<String> treeitem = new TreeItem<>("AAAA");
            showFiles.setRoot(treeitem);

            for (Metadata metadata : entries) {
                if (metadata instanceof FileMetadata) {
                    treeitem.getChildren().add(new TreeItem<>(metadata.getName()));
                }
            }

        } catch (Exception e) {

        }
    }

    @FXML
    void downloadItem(ActionEvent event) {
        TreeItem<String> item = (TreeItem<String>) showFiles.getSelectionModel().getSelectedItem();

        try {
            path += item.getValue();

            DbxDownloader<FileMetadata> downloader = client.files().download(path);

            FileOutputStream out = new FileOutputStream(item.getValue());
            downloader.download(out);
            out.close();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @FXML
    void uploadItem(ActionEvent event) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(null);

        System.out.println(fileChooser.getSelectedFile().toPath().toString());
        String fileName = fileChooser.getSelectedFile().getName();
        System.out.println(fileName);
        String uploadFilePath = fileChooser.getSelectedFile().toPath().toString();
        // UploadFile uploadFile = new UploadFile();
        // uploadFile.uploadFileFunc(uploadFilePath);

        try {
            try (InputStream in = new FileInputStream(uploadFilePath)) {
                client.files().uploadBuilder(path + fileName).uploadAndFinish(in);
            }
        } catch (DbxException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @FXML
    void getPath(MouseEvent event) throws IOException {
        System.out.println(files.getText());

        // download.downloadFile(files.getText());
    }

}