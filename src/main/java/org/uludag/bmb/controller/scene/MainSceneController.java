package org.uludag.bmb.controller.scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.dropbox.core.DbxException;
import com.dropbox.core.json.JsonReader.FileLoadException;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.sharing.UserFileMembershipInfo;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.dataproperty.NotificationListCellFactory;
import org.uludag.bmb.beans.dataproperty.StyledHyperLink;
import org.uludag.bmb.beans.dataproperty.CloudFileProperty;
import org.uludag.bmb.controller.config.ConfigController;
import org.uludag.bmb.operations.dropbox.Client;
import org.uludag.bmb.operations.dropbox.FileOperations;
import org.uludag.bmb.operations.scenedatasource.UITrees;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainSceneController extends Controller implements Initializable {
    @FXML
    public Button btnDownload;

    @FXML
    public Button btnNewFolder;

    @FXML
    public Button btnUpload;

    @FXML
    public Button btnClearNotifications;

    @FXML
    public Button btnNotificationn;

    @FXML
    public TabPane tabPane;

    @FXML
    public TreeView<String> treeView;

    @FXML
    public Pane notificationPane;

    @FXML
    public Circle notificationDot;

    @FXML
    public SplitPane pathBar;

    @FXML
    public TableView<CloudFileProperty> cloudTableView;

    @FXML
    public TableColumn<CloudFileProperty, ArrayList<String>> ctwAccess;

    @FXML
    public Pane fileDetailPane;

    @FXML
    public TableColumn<CloudFileProperty, CheckBox> ctwCheckBox;

    @FXML
    public TableColumn<CloudFileProperty, String> ctwFilePath;

    @FXML
    public TableColumn<CloudFileProperty, String> ctwFileName;

    @FXML
    public TableColumn<CloudFileProperty, Date> ctwLastEdit;

    @FXML
    public TableColumn<CloudFileProperty, CheckBox> ctwChange;

    @FXML
    public TableColumn<CloudFileProperty, CheckBox> ctwDownload;

    @FXML
    public ListView<String> notificationList;

    @FXML
    public Label lblFileName;

    @FXML
    public Label lblFileSize;

    @FXML
    public Label lblLastEdit;

    @FXML
    public ListView<String> shareList;

    @FXML
    public Tooltip fileNameTTip;

    @FXML
    public Tooltip fileSizeTTip;

    @FXML
    public Tooltip lastChangeTTip;

    @FXML
    public ImageView fileIcon;

    @FXML
    public ListView<String> sharedFilesList;

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
        notificationList.setCellFactory(param -> new NotificationListCellFactory());
        /*
         * Timeline notificationCycle = new Timeline(
         * new KeyFrame(Duration.seconds(2),
         * new EventHandler<ActionEvent>() {
         * 
         * @Override
         * public void handle(ActionEvent event) {
         * List<String> notifications = notificationOperations.getNotifications();
         * if (notifications.size() != 0 && notifications != null) {
         * try {
         * String path = "/";
         * for (int i = 1; i < linkPane.getItems().size(); i++) {
         * path += ((Hyperlink) linkPane.getItems().get(i)).getText();
         * }
         * var items = UITrees.GET_FILES(path);
         * cloudTableView.setItems(items);
         * cloudTableView.refresh();
         * } catch (IndexOutOfBoundsException e) {
         * 
         * }
         * for (String notification : notifications) {
         * notificationList.getItems().add(0, notification);
         * notificationDot.visibleProperty().set(true);
         * ScaleTransition st = new ScaleTransition(Duration.millis(1000),
         * notificationDot);
         * st.setFromX(0.6);
         * st.setFromY(0.6);
         * st.setToX(1.2);
         * st.setToY(1.2);
         * st.setCycleCount(3);
         * st.setAutoReverse(true);
         * st.jumpTo(Duration.millis(200));
         * st.play();
         * }
         * notifications.clear();
         * }
         * 
         * }
         * }));
         * notificationCycle.setCycleCount(Timeline.INDEFINITE);
         * notificationCycle.play();
         * 
         * notificationPane.visibleProperty().set(false);
         */
        TreeItem<String> root = UITrees.Hierarchy.getAsTreeItem("");
        treeView.setRoot(root);
        treeView.setShowRoot(false);

        // List<SharedFileMetadata> entries;
        // try {
        // entries = Client.client.sharing().listReceivedFiles().getEntries();
        // if (entries.size() != 0) {
        // for (SharedFileMetadata entry : entries) {
        // SharedFile sharedFile =
        // publicInfoOperations.getSharedFileByEncryptedName(entry.getName());
        // Crypto.SHARE.DECRYPT_PREVIEW(sharedFile);
        // String decryptedName =
        // fileRecordOperations.getSharedRecordPreview(entry.getName())
        // .getDecryptedName();
        // sharedFilesList.getItems().add(decryptedName);
        // //paylaşılan dosyadan çıkılacak
        // }
        // }
        // } catch (DbxException e) {
        // e.printStackTrace();
        // }

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
        notificationPane.visibleProperty().set(false);
        fileDetailPane.setLayoutY(34);
    }

    @FXML
    void deleteSelectedFiles(ActionEvent event) {
        for (CloudFileProperty file : cloudTableView.getSelectionModel().getSelectedItems()) {
            FileOperations.DELETE_FILE(file);
        }
    }

    @FXML
    void changeSyncStatusOn(ActionEvent event) {
        List<CloudFileProperty> selectedItems = cloudTableView.getSelectionModel().getSelectedItems();
        for (CloudFileProperty item : selectedItems) {
            item.selection().get().selectedProperty().set(true);
            FileOperations.CHANGE_STATUS(item, true);
        }
    }

    @FXML
    void changeSyncStatusOff(ActionEvent event) {
        List<CloudFileProperty> selectedItems = cloudTableView.getSelectionModel().getSelectedItems();
        for (CloudFileProperty item : selectedItems) {
            item.selection().get().selectedProperty().set(false);
            FileOperations.CHANGE_STATUS(item, false);
        }
    }

    @FXML
    void hierarchySelectFolder(MouseEvent event) {
        TreeItem<String> selectedFolder = (TreeItem<String>) treeView.getSelectionModel().getSelectedItem();
        try {
            StringBuilder folderPath = new StringBuilder();
            for (; selectedFolder.getParent() != null; selectedFolder = selectedFolder.getParent()) {
                folderPath.insert(0, selectedFolder.getValue() + "/");
            }
            ObservableList<CloudFileProperty> files = fileRecordOperations.getByPath(folderPath.toString());
            cloudTableView.setItems(files);
            cloudTableView.refresh();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    pathBar.getItems().clear();
                    List<String> selectedBarPath = Arrays.asList(folderPath.toString().split("/"));
                    if (selectedBarPath.size() == 0) {
                        pathBar.getItems().add(new StyledHyperLink(pathBar, cloudTableView, ""));
                        return;
                    }
                    for (String pathPart : Arrays.asList(folderPath.toString().split("/"))) {
                        pathBar.getItems().add(new StyledHyperLink(pathBar, cloudTableView, pathPart));
                    }

                }
            });
        } catch (NullPointerException e) {
            // catches if item expand arrow is selected instead of selecting item
            // so no action will take place
        }
    }

    @FXML
    void downloadSelectedFile(ActionEvent event) {
        for (var file : cloudTableView.getSelectionModel().getSelectedItems()) {
            FileOperations.DOWNLOAD_FILE(file.getFilePath(), file.getFileName());
        }
    }

    @FXML
    void uploadSelectedFile(ActionEvent event) throws IOException, UploadErrorException, DbxException {
        var folderPathNode = pathBar.getItems();
        String uploadDirectory = "/";
        if (folderPathNode.size() != 0) {
            for (int index = 1; index < folderPathNode.size(); index++) {
                uploadDirectory += ((Hyperlink) pathBar.getItems().get(index)).getText().toString();
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
    void showSelectedFileDetails(MouseEvent event) {
        try {
            CloudFileProperty selectedFiles = cloudTableView.getSelectionModel().getSelectedItem();
            String fileExtension = selectedFiles.getFileName().split(Pattern.quote("."))[1];
            if (fileExtension.equals("png") || fileExtension.equals("jpg") || fileExtension.equals("jpeg")
                    || fileExtension.equals("mp4") || fileExtension.equals("mp3") || fileExtension.equals("svg")) {
                File icon = new File(MainSceneController.class.getResource("/icons/mediaIcon.png").getPath());
                fileIcon.setImage(new Image(new FileInputStream(icon)));
            } else if (fileExtension.equals("pdf")) {
                File icon = new File(MainSceneController.class.getResource("/icons/pdfIcon.png").getPath());
                fileIcon.setImage(new Image(new FileInputStream(icon)));
            } else if (fileExtension.equals("txt") || fileExtension.equals("docx") || fileExtension.equals("doc")) {
                File icon = new File(MainSceneController.class.getResource("/icons/textIcon.png").getPath());
                fileIcon.setImage(new Image(new FileInputStream(icon)));
            } else {
                File icon = new File(MainSceneController.class.getResource("/icons/defaultIcon.png").getPath());
                fileIcon.setImage(new Image(new FileInputStream(icon)));
            }
            shareList.getItems().clear();
            lblFileName.setText(selectedFiles.getFileName());
            lblLastEdit.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(selectedFiles.getLastEditDate()));
            lblFileSize.setText(String.valueOf(
                    (FileOperations.GET_METADATA(selectedFiles.getFilePath(), selectedFiles.getFileName()).getSize())
                            / (1024))
                    + " KB");
            String idShared = FileOperations.GET_METADATA(selectedFiles.getFilePath(), selectedFiles.getFileName())
                    .getId();
            List<UserFileMembershipInfo> sharedPeople = Client.client.sharing().listFileMembers(idShared)
                    .getUsers();
            for (UserFileMembershipInfo membershipInfo : sharedPeople) {
                shareList.getItems().add(membershipInfo.getUser().getEmail());
            }

            fileNameTTip.setText(lblFileName.getText());
            fileSizeTTip.setText(lblFileSize.getText());
            lastChangeTTip.setText(lblLastEdit.getText());
            lblFileName.setTooltip(fileNameTTip);
            lblFileSize.setTooltip(fileSizeTTip);
            lblLastEdit.setTooltip(lastChangeTTip);
        } catch (Exception e) {
        }

    }

    @FXML
    void saveSelectedSharedFile(ActionEvent event) {
        new SaveSharedFileSceneController(this, "saveSharedFileScene", "Paylaşılan Dosyayı Kaydet");
    }

    @FXML
    void createNewFolder(MouseEvent event) {
        new FolderCreationSceneController(this, "folderCreationScene", "Yeni Klasör Oluştur");
    }

    @FXML
    void shareSelectedFiles(ActionEvent event) {
        new ShareWindowController(this, "shareFilesScene", "Dosya Paylaş");
    }
}