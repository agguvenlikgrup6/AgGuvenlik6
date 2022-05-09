package org.uludag.bmb.controller.scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;

import com.dropbox.core.DbxException;
import com.dropbox.core.json.JsonReader.FileLoadException;
import com.dropbox.core.v2.files.UploadErrorException;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.filedata.FileDataProperty;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.operations.UpDown;
import org.uludag.bmb.operations.UITrees;
import org.uludag.bmb.sync.SyncServer;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainSceneController extends Controller implements Initializable {
    @FXML
    private Button btnDownload;

    @FXML
    private Button btnUpload;

    @FXML
    private TabPane tabPane;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private Font x1;

    @FXML
    private SplitPane linkPane;

    @FXML
    private TableView<FileDataProperty> cloudTableView;

    @FXML
    private TableColumn<FileDataProperty, ArrayList<String>> ctwAccess;

    @FXML
    private TableColumn<FileDataProperty, CheckBox> ctwCheckBox;

    @FXML
    private TableColumn<FileDataProperty, String> ctwFilePath;

    @FXML
    private TableColumn<FileDataProperty, String> ctwFileName;

    @FXML
    private TableColumn<FileDataProperty, Date> ctwLastEdit;

    @FXML
    private TableColumn<FileDataProperty, Boolean> ctwSyncStatus;

    public MainSceneController() throws FileLoadException {
        super(PropertiesReader.getProperty("mainSceneFxml"),
                Integer.parseInt(PropertiesReader.getProperty("mainSceneWidth")),
                Integer.parseInt(PropertiesReader.getProperty("mainSceneHeigth")));
    }

    @Override
    public void displayScene(Stage stage) {
        this.stage = stage;
        stage.setScene(scene);
        stage.hide();
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<String> root = UITrees.Hierarchy.getAsTreeItem("");
        treeView.setRoot(root);
        treeView.setShowRoot(false);

        cloudTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ctwFileName.setCellValueFactory(cellData -> cellData.getValue().fileName());
        ctwLastEdit.setCellValueFactory(cellData -> cellData.getValue().lastEditDate());
        ctwSyncStatus.setCellValueFactory(cellData -> cellData.getValue().syncStatus());
        ctwCheckBox.setCellValueFactory(cellData -> cellData.getValue().selection());
        ctwFilePath.setCellValueFactory(cellData -> cellData.getValue().filePath());
    }

    @FXML
    void listSelectFiles(MouseEvent event) {
    }

    @FXML
    void shareFile(ActionEvent event) {
        ObservableList<FileDataProperty> selectedFiles = cloudTableView.getSelectionModel().getSelectedItems();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Controller.class.getResource("/shareScene.fxml"));
            fxmlLoader.setController(new ShareWindowController());
            ShareWindowController controller = fxmlLoader.getController();
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Dosya Paylaş");
            Scene newScene = new Scene(root);
            newScene.getStylesheets().add(PropertiesReader.getProperty("shareSceneCss"));
            stage.setScene(newScene);
            stage.show();

            controller.setFileList(selectedFiles);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void deleteFile(ActionEvent event) {
        System.out.println("delete");
    }

    @FXML
    void hierarchySelectFolder(MouseEvent event) {
        new Thread(() -> {
            System.out.println(SyncServer.getSyncStatus());
            ArrayList<String> path = new ArrayList<String>();
            ArrayList<String> pathNaked = new ArrayList<String>();

            TreeItem<String> selectedFolder = (TreeItem<String>) treeView.getSelectionModel().getSelectedItem();
            var item = selectedFolder;

            if (item != null) {
                while (item.getParent() != null) {
                    path.add(item.getValue() + "/");
                    pathNaked.add(item.getValue() + "/");
                    item = item.getParent();
                }
                path.add("/");
                Collections.reverse(path);
                Collections.reverse(pathNaked);
                var items = UITrees.CLOUD_FILES(path);
                cloudTableView.setItems(items);
                cloudTableView.refresh();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        addToPathLink(pathNaked);
                    }
                });
            }
        }).start();
    }

    private void addToPathLink(ArrayList<String> path) {
        linkPane.getItems().clear();

        Hyperlink pathPart = new Hyperlink("Dropbox/");
        pathPart.getStyleClass().add("pathPart");
        pathPart.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                cloudTableView.setItems(UITrees.CLOUD_FILES(""));
                treeView.getSelectionModel().select(0);
                linkPane.getItems().remove(1, linkPane.getItems().size());
            }
        });
        linkPane.getItems().add(pathPart);

        for (int i = 1; i < path.size(); i++) {
            pathPart = new Hyperlink(path.get(i));
            pathPart.getStyleClass().add("pathPart");
            pathPart.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    int selectedPathIndex = linkPane.getItems().indexOf(event.getSource());
                    int pathSize = linkPane.getItems().size();
                    for (int i = 0; i < pathSize - (selectedPathIndex + 1); i++) {
                        treeView.getSelectionModel().selectPrevious();
                    }

                    String pathS = "/";
                    for (int i = 1; i <= linkPane.getItems().indexOf(event.getSource()); i++) {
                        pathS += ((Hyperlink) linkPane.getItems().get(i)).getText();
                    }
                    cloudTableView.setItems(UITrees.CLOUD_FILES(pathS));
                    linkPane.getItems().remove(linkPane.getItems().indexOf(event.getSource()) + 1,
                            linkPane.getItems().size());
                }

            });
            linkPane.getItems().add(pathPart);
        }

    }

    @FXML
    void downloadItem(ActionEvent event) {
        ObservableList<FileDataProperty> selectedFiles = cloudTableView.getSelectionModel().getSelectedItems();
        String localPath = ConfigController.Settings.LoadSettings().getLocalDropboxPath();

        for (var file : selectedFiles) {
            String fileWithPath = localPath + file.getFilePath() + "/" + file.getFileName();
            // eğer dosya yoksa
            if (!Files.exists(Paths.get(fileWithPath))) {
                // eğer dosyanın bulunduğu klasör yoksa
                if (!Files.exists(Paths.get(localPath + file.getFilePath()))) {
                    File fileFolder = new File(localPath + file.getFilePath());
                    fileFolder.mkdirs();
                }
                new Thread(() -> {
                    UpDown.DOWNLOAD_FILE(localPath, file.getFilePath(), "/" + file.getFileName());
                }).start();
            } else {
                System.out.println("Dosya zaten indirilmiş!");
            }
        }
    }

    @FXML
    void uploadItem(ActionEvent event) throws IOException, UploadErrorException, DbxException {
        var folderPathNode = linkPane.getItems();
        String uploadDirectory = "/";
        for (int index = 1; index < folderPathNode.size(); index++) {
            uploadDirectory += ((Hyperlink) linkPane.getItems().get(index)).getText().toString();
        }

        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);

        UpDown.UPLOAD_FILE(uploadDirectory, selectedFile);

    }

    @FXML
    void getPath(MouseEvent event) throws IOException {

    }
}