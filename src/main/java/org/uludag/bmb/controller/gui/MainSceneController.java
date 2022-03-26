package org.uludag.bmb.controller.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;


import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.entity.gui.DropboxFilePath;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainSceneController extends Controller implements Initializable {
    String path;

    @FXML
    private Button btnDownload;

    @FXML
    private ListView<?> fileList;
    
    @FXML
    private Button btnUpload;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private Text files;

    @FXML
    private Font x1;

    @FXML
    private Color x2;

    @FXML
    private Font x3;

    @FXML
    private Color x4;

    public MainSceneController () {
        try {
            fxmlLoad(PropertiesReader.getProperty("mainSceneFxml"),
                    Integer.parseInt(PropertiesReader.getProperty("mainSceneWidth")),
                    Integer.parseInt(PropertiesReader.getProperty("mainSceneHeigth")));
        } catch (NumberFormatException | IOException e) {
            // TODO handle exception
        }
    }

    public void displayHomeScreen(Stage stage) {
        this.stage = stage;

        stage.hide();
        stage.show();
    }


    private Set<String> stringSet;
    ObservableList observableList = FXCollections.observableArrayList();

    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var tree = new DropboxFilePath(client).getTree();
        treeView.setRoot(tree);
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

            ListFolderResult result = client.getClient().files().listFolder(path);

            List<Metadata> entries = result.getEntries();

            TreeItem<String> treeitem = new TreeItem<>("AAAA");
            // showFiles.setRoot(treeitem);

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
        // TreeItem<String> item = (TreeItem<String>) showFiles.getSelectionModel().getSelectedItem();

        // try {
        //     path += item.getValue();

        //     DbxDownloader<FileMetadata> downloader = client.getClient().files().download(path);

        //     FileOutputStream out = new FileOutputStream(item.getValue());
        //     downloader.download(out);
        //     out.close();

        // } catch (Exception e) {
        //     // TODO: handle exception
        // }
    }

    @FXML
    void uploadItem(ActionEvent event) throws IOException {
        // directoryChooser.getAb
        // JFileChooser fileChooser = new JFileChooser();
        // fileChooser.showOpenDialog(null);

        // System.out.println(fileChooser.getSelectedFile().toPath().toString());
        // String fileName = fileChooser.getSelectedFile().getName();
        // System.out.println(fileName);
        // String uploadFilePath = fileChooser.getSelectedFile().toPath().toString();
        // // UploadFile uploadFile = new UploadFile();
        // // uploadFile.uploadFileFunc(uploadFilePath);

        // try {
        //     try (InputStream in = new FileInputStream(uploadFilePath)) {
        //         client.getClient().files().uploadBuilder(path + fileName).uploadAndFinish(in);
        //     }
        // } catch (DbxException exception) {
        //     System.err.println(exception.getMessage());
        // }
    }

    @FXML
    void getPath(MouseEvent event) throws IOException {
        System.out.println(files.getText());

        // download.downloadFile(files.getText());
    }

    

}