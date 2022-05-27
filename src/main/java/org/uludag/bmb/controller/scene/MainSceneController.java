package org.uludag.bmb.controller.scene;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.dropbox.core.DbxException;
import com.dropbox.core.json.JsonReader.FileLoadException;
import com.dropbox.core.v2.files.UploadErrorException;


import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.dataproperty.NotificationListCellFactory;
import org.uludag.bmb.beans.dataproperty.TableViewDataProperty;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.controller.database.DatabaseController;
import org.uludag.bmb.operations.dropbox.Client;
import org.uludag.bmb.operations.dropbox.FileOperations;
import org.uludag.bmb.operations.scenedatasource.UITrees;
import org.uludag.bmb.service.sync.SyncServer;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainSceneController extends Controller implements Initializable {
    private DatabaseController dc;

    @FXML
    private Button btnDownload;

    @FXML
    private Button btnNewFolder;

    @FXML
    private Button btnUpload;

    @FXML
    private Button btnClearNotifications;

    @FXML
    private Button btnNotificationn;

    @FXML
    private TabPane tabPane;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private Pane notificationPane;

    @FXML
    private Circle notificationDot;

    @FXML
    private SplitPane linkPane;

    @FXML
    private TableView<TableViewDataProperty> cloudTableView;

    @FXML
    private TableColumn<TableViewDataProperty, ArrayList<String>> ctwAccess;

    @FXML
    private Pane fileDetailPane;

    @FXML
    private TableColumn<TableViewDataProperty, CheckBox> ctwCheckBox;

    @FXML
    private TableColumn<TableViewDataProperty, String> ctwFilePath;

    @FXML
    private TableColumn<TableViewDataProperty, String> ctwFileName;

    @FXML
    private TableColumn<TableViewDataProperty, Date> ctwLastEdit;

    @FXML
    private TableColumn<TableViewDataProperty, CheckBox> ctwChange;

    @FXML
    private TableColumn<TableViewDataProperty, CheckBox> ctwDownload;

    @FXML
    public ListView<String> notificationList;

    @FXML
    private Label lblFileName;

    @FXML
    private Label lblFileSize;

    @FXML
    private Label lblLastEdit;
    @FXML
    private ListView<String> shareList;
   
    @FXML
    private Tooltip fileNameTTip;

    @FXML
    private Tooltip fileSizeTTip;

    @FXML
    private Tooltip lastChangeTTip;



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
        dc = new DatabaseController();
        notificationList.setCellFactory(param -> new NotificationListCellFactory());

        Timeline notificationCycle = new Timeline(
                new KeyFrame(Duration.seconds(2),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                List<String> notifications = dc.getNotifications();
                                if (notifications.size() != 0 && notifications != null) {
                                    try {
                                        String path = "/";
                                        for (int i = 1; i < linkPane.getItems().size(); i++) {
                                            path += ((Hyperlink) linkPane.getItems().get(i)).getText();
                                        }
                                        var items = UITrees.LOCAL_FILES(path);
                                        cloudTableView.setItems(items);
                                        cloudTableView.refresh();
                                    } catch (IndexOutOfBoundsException e) {

                                    }
                                    for (String notification : notifications) {
                                        notificationList.getItems().add(0, notification);
                                        notificationDot.visibleProperty().set(true);
                                        ScaleTransition st = new ScaleTransition(Duration.millis(1000),
                                                notificationDot);
                                        st.setFromX(0.6);
                                        st.setFromY(0.6);
                                        st.setToX(1.2);
                                        st.setToY(1.2);
                                        st.setCycleCount(3);
                                        st.setAutoReverse(true);
                                        st.jumpTo(Duration.millis(200));
                                        st.play();
                                    }
                                    notifications.clear();
                                }
                            }
                        }));
        notificationCycle.setCycleCount(Timeline.INDEFINITE);
        notificationCycle.play();

        notificationPane.visibleProperty().set(false);
        TreeItem<String> root = UITrees.Hierarchy.getAsTreeItem("");
        treeView.setRoot(root);
        treeView.setShowRoot(false);

        cloudTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ctwFileName.setCellValueFactory(cellData -> cellData.getValue().fileName());
        ctwLastEdit.setCellValueFactory(cellData -> cellData.getValue().lastEditDate());
        ctwFilePath.setCellValueFactory(cellData -> cellData.getValue().filePath());
        ctwCheckBox.setCellValueFactory(cellData -> cellData.getValue().selection());
        ctwCheckBox.setEditable(false);
        ctwChange.setCellValueFactory(cellData -> cellData.getValue().changeStatus());
        ctwChange.setEditable(false);
        ctwDownload.setCellValueFactory(cellData -> cellData.getValue().downloadStatus());
        ctwDownload.setEditable(false);
    }

    @FXML
    void clearNotifications(MouseEvent event) {
        notificationList.getItems().clear();
        notificationDot.visibleProperty().set(false);
    }

    @FXML
    void shareFile(ActionEvent event) {
        ObservableList<TableViewDataProperty> selectedFiles = cloudTableView.getSelectionModel().getSelectedItems();
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
    void createNewFolder(MouseEvent event) {
        var folderPathNode = linkPane.getItems();
        String uploadDirectory = "/";
        if (folderPathNode.size() != 0) {
            for (int index = 1; index < folderPathNode.size(); index++) {
                uploadDirectory += ((Hyperlink) linkPane.getItems().get(index)).getText().toString();
            }
        }

        TextInputDialog td = new TextInputDialog();
        td.titleProperty().set("Yeni Klasör Oluştur");
        td.setHeaderText("Yeni Klasör İsmi:");
        td.showAndWait();

        String folderName = td.getEditor().getText();
        if (folderName != "") {
            try {
                Client.client.files().createFolderV2(uploadDirectory + folderName);
                TreeItem<String> root = UITrees.Hierarchy.getAsTreeItem("");
                treeView.setRoot(root);
                treeView.setShowRoot(false);
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    void deleteFile(ActionEvent event) {
        List<TableViewDataProperty> selectedItems = cloudTableView.getSelectionModel().getSelectedItems();
        for (TableViewDataProperty item : selectedItems) {
            if (item.getSync()) {
                FileOperations.DELETE_FROM_CLOUD(item.getFilePath(), item.getFileName());
            } else {
                FileOperations.DELETE_FROM_LOCAL(item.getFilePath(), item.getFileName());
            }
        }
    }

    @FXML
    void changeSyncStatusOn(ActionEvent event) {
        List<TableViewDataProperty> selectedItems = cloudTableView.getSelectionModel().getSelectedItems();
        for (TableViewDataProperty item : selectedItems) {
            item.selection().get().selectedProperty().set(true);
            dc.changeSyncStatus(item, true);
            FileOperations.CHANGE_STATUS(item.getFilePath(), item.getFileName(), true);
        }
    }

    @FXML
    void changeSyncStatusOff(ActionEvent event) {
        List<TableViewDataProperty> selectedItems = cloudTableView.getSelectionModel().getSelectedItems();
        for (TableViewDataProperty item : selectedItems) {
            item.selection().get().selectedProperty().set(false);
            dc.changeSyncStatus(item, false);
            FileOperations.CHANGE_STATUS(item.getFilePath(), item.getFileName(), false);
        }
    }

    @FXML
    void hierarchySelectFolder(MouseEvent event) {
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
            Collections.reverse(path);
            Collections.reverse(pathNaked);
            var items = UITrees.LOCAL_FILES(String.join("", path));
            cloudTableView.setItems(items);
            cloudTableView.refresh();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    addToPathLink(pathNaked);
                }
            });
        }
    }

    private void addToPathLink(ArrayList<String> path) {
        linkPane.getItems().clear();

        Hyperlink pathPart = new Hyperlink("Dropbox/");
        pathPart.getStyleClass().add("pathPart");
        pathPart.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                cloudTableView.setItems(UITrees.LOCAL_FILES("/"));
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
                    String pathS = "";
                    for (int i = 1; i <= linkPane.getItems().indexOf(event.getSource()); i++) {
                        pathS += ((Hyperlink) linkPane.getItems().get(i)).getText();
                    }
                    cloudTableView.setItems(UITrees.LOCAL_FILES("/" + pathS));
                    linkPane.getItems().remove(linkPane.getItems().indexOf(event.getSource()) + 1,
                            linkPane.getItems().size());
                }

            });
            linkPane.getItems().add(pathPart);
        }

    }

    @FXML
    void downloadItem(ActionEvent event) {
        ObservableList<TableViewDataProperty> selectedFiles = cloudTableView.getSelectionModel().getSelectedItems();
        if (selectedFiles.size() != 0) {
            String localPath = ConfigController.Settings.LoadSettings().getLocalDropboxPath();

            for (var file : selectedFiles) {
                FileOperations.DOWNLOAD_FILE(localPath, file.getFilePath(), file.getFileName());
            }
        }
    }

    @FXML
    void uploadItem(ActionEvent event) throws IOException, UploadErrorException, DbxException {
        var folderPathNode = linkPane.getItems();
        String uploadDirectory = "/";
        if (folderPathNode.size() != 0) {
            for (int index = 1; index < folderPathNode.size(); index++) {
                uploadDirectory += ((Hyperlink) linkPane.getItems().get(index)).getText().toString();
            }
        }

        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        FileOperations.UPLOAD_FILE(uploadDirectory, selectedFile);
    }

    @FXML
    void showNotifications(MouseEvent event) {
        if (!notificationPane.visibleProperty().get()) {
            notificationPane.visibleProperty().set(true);
            fileDetailPane.setLayoutY(360);
        } else {
            notificationPane.visibleProperty().set(false);
            fileDetailPane.setLayoutY(34);
        }

    }
    @FXML
    void showFileDetails(MouseEvent event) {

        ObservableList<TableViewDataProperty> selectedFiles = cloudTableView.getSelectionModel().getSelectedItems();
        for (var file : selectedFiles) {
            lblFileName.setText(file.getFileName());
            lblLastEdit.setText(file.getLastEditDate().toString());
            lblFileSize.setText(String.valueOf((FileOperations.GET_METADATA(file.getFilePath(),file.getFileName()).getSize())/(1024))+" KB");
            shareList.getItems().addAll(String.valueOf((FileOperations.GET_METADATA(file.getFilePath(), file.getFileName())).getSharingInfo()));
            System.out.println(123);
        }
       
        fileNameTTip.setText(lblFileName.getText());
        fileSizeTTip.setText(lblFileSize.getText());
        lastChangeTTip.setText(lblLastEdit.getText());
        lblFileName.setTooltip(fileNameTTip);
        lblFileSize.setTooltip(fileSizeTTip);
        lblLastEdit.setTooltip(lastChangeTTip);

    }
    
}