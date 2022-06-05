package org.uludag.bmb.controller.scene;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.regex.Pattern;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.beans.dataproperty.CustomHyperLink;
import org.uludag.bmb.beans.dataproperty.CustomNotificationListCell;
import org.uludag.bmb.beans.dataproperty.CustomRecievedFileListView;
import org.uludag.bmb.beans.dataproperty.CustomTableView;
import org.uludag.bmb.operations.FileOperations;
import org.uludag.bmb.operations.scenedatasource.UITrees;

import com.dropbox.core.DbxException;
import com.dropbox.core.json.JsonReader.FileLoadException;
import com.dropbox.core.v2.files.UploadErrorException;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainSceneController extends SceneController implements Initializable {
    @FXML
    public Button btnDownload;

    @FXML
    public Button btnNewFolder;

    @FXML
    public Button btnUpload;

    @FXML
    public Button btnClearNotifications;

    @FXML
    public Button btnShowNotification;

    @FXML
    public TreeView<String> directoriesHierarchyView;

    @FXML
    public Pane notificationPane;

    @FXML
    public Circle notificationDot;

    @FXML
    public SplitPane selectedDirectoryPathPane;

    @FXML
    public TableView<CustomTableView> fileListView;

    @FXML
    public Pane fileDetailPane;

    @FXML
    public TableColumn<CustomTableView, CheckBox> syncStatusColumn;

    @FXML
    public TableColumn<CustomTableView, String> filePathColumn;

    @FXML
    public TableColumn<CustomTableView, String> fileNameColumn;

    @FXML
    public TableColumn<CustomTableView, Date> modificationDateColumn;

    @FXML
    public TableColumn<CustomTableView, CheckBox> fileChangeStatusColumn;

    @FXML
    public TableColumn<CustomTableView, CheckBox> fileDownloadStatusColumn;

    @FXML
    public ListView<String> notificationListView;

    @FXML
    public Label detailFileName;

    @FXML
    public Label detailFileSize;

    @FXML
    public Label detailFilePath;

    @FXML
    public Label detailModificationDate;

    @FXML
    public ListView<String> fileAccessorsListView;

    @FXML
    public Tooltip toolTipFileName;

    @FXML
    public Tooltip toolTipFileSize;

    @FXML
    public Tooltip toolTipFilePath;

    @FXML
    public Tooltip toolTipModificationDate;

    @FXML
    public Button fileIcon;

    @FXML
    public Label lblSenderEmail;

    @FXML
    public Label lblSender;

    @FXML
    public TableView<CustomRecievedFileListView> recievedFilesList;

    @FXML
    public TableColumn<CustomRecievedFileListView, String> recievedFileSenderEmail;

    @FXML
    public TableColumn<CustomRecievedFileListView, String> recievedFileDecryptedName;

    public MainSceneController() throws FileLoadException {
        super(PropertiesReader.getProperty("mainSceneFxml"),
                Integer.parseInt(PropertiesReader.getProperty("mainSceneWidth")),
                Integer.parseInt(PropertiesReader.getProperty("mainSceneHeigth")));
        refreshRecievedFileList();
    }

    @Override
    public void displayScene(Stage stage) {
        try {
            this.stage = stage;
            stage.setScene(scene);
            stage.hide();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<String> root = UITrees.Hierarchy.getAsTreeItem("");
        directoriesHierarchyView.setRoot(root);
        directoriesHierarchyView.setShowRoot(false);

        notificationListView.setCellFactory(param -> new CustomNotificationListCell());

        fileListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        new NotificationPaneController(this);
    }

    @FXML
    void clearNotifications(MouseEvent event) {
        notificationListView.getItems().clear();
        notificationDot.visibleProperty().set(false);
        notificationPane.visibleProperty().set(false);
        fileDetailPane.setLayoutY(34);
    }

    @FXML
    void deleteSelectedFiles(ActionEvent event) {
        for (CustomTableView file : fileListView.getSelectionModel().getSelectedItems()) {
            FileOperations.deleteFile(file);
        }
    }

    @FXML
    void changeSyncStatusOn(ActionEvent event) {
        List<CustomTableView> selectedItems = fileListView.getSelectionModel().getSelectedItems();
        for (CustomTableView item : selectedItems) {
            item.syncStatus().get().selectedProperty().set(true);
            FileOperations.changeSyncStatus(item, true);
        }
    }

    @FXML
    void changeSyncStatusOff(ActionEvent event) {
        List<CustomTableView> selectedItems = fileListView.getSelectionModel().getSelectedItems();
        for (CustomTableView item : selectedItems) {
            item.syncStatus().get().selectedProperty().set(false);
            FileOperations.changeSyncStatus(item, false);
        }
    }

    @FXML
    void hierarchySelectFolder(MouseEvent event) {
        fileIcon.getStyleClass().clear();
        fileIcon.getStyleClass().addAll("button", "iconFolder");
        TreeItem<String> selectedFolder = (TreeItem<String>) directoriesHierarchyView.getSelectionModel()
                .getSelectedItem();
        try {
            StringBuilder folderPath = new StringBuilder();
            for (; selectedFolder.getParent() != null; selectedFolder = selectedFolder.getParent()) {
                folderPath.insert(0, selectedFolder.getValue() + "/");
            }
            ObservableList<CustomTableView> files = fileRecordOperations.getByPath(folderPath.toString());
            fileListView.setItems(files);
            fileListView.refresh();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    selectedDirectoryPathPane.getItems().clear();
                    List<String> selectedBarPath = Arrays.asList(folderPath.toString().split("/"));
                    if (selectedBarPath.size() == 0) {
                        selectedDirectoryPathPane.getItems()
                                .add(new CustomHyperLink(selectedDirectoryPathPane, fileListView, ""));
                        return;
                    }
                    for (String pathPart : Arrays.asList(folderPath.toString().split("/"))) {
                        selectedDirectoryPathPane.getItems()
                                .add(new CustomHyperLink(selectedDirectoryPathPane, fileListView, pathPart));
                    }

                }
            });
        } catch (NullPointerException e) {
        }
    }

    @FXML
    void downloadSelectedFile(ActionEvent event) {
        for (var file : fileListView.getSelectionModel().getSelectedItems()) {
            FileOperations.downloadFile(file.getFilePath(), file.getFileName());
        }
    }

    @FXML
    void uploadSelectedFile(ActionEvent event) throws IOException, UploadErrorException, DbxException {
        var folderPathNode = selectedDirectoryPathPane.getItems();
        String uploadDirectory = "/";
        if (folderPathNode.size() != 0) {
            for (int index = 1; index < folderPathNode.size(); index++) {
                uploadDirectory += ((Hyperlink) selectedDirectoryPathPane.getItems().get(index)).getText().toString();
            }
        }

        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        FileOperations.uploadFile(uploadDirectory, selectedFile);
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
            CustomTableView selectedFile = fileListView.getSelectionModel().getSelectedItem();
            fileIcon.getStyleClass().clear();
            String fileExtension = selectedFile.getFileName().split(Pattern.quote("."))[1];
            switch (fileExtension) {
                case "png":
                case "svg":
                case "jpeg":
                case "jpg":
                    fileIcon.getStyleClass().addAll("button", "iconMedia");
                    break;
                case "pdf":
                    fileIcon.getStyleClass().addAll("button", "iconPdf");
                    break;
                case "xml":
                case "json":
                case "java":
                    fileIcon.getStyleClass().addAll("button", "iconCode");
                    break;
                case "txt":
                case "docx":
                case "doc":
                    fileIcon.getStyleClass().addAll("button", "iconText");
                    break;
                default:
                    fileIcon.getStyleClass().addAll("button", "iconDefault");
                    break;
            }
            FileRecord selectedFileRecord = fileRecordOperations.getByPathAndName(selectedFile.getFilePath(),
                    selectedFile.getFileName());
            detailFileName.setText(selectedFileRecord.getName());
            detailFileSize.setText(selectedFileRecord.getFileSize());
            detailFilePath.setText(selectedFileRecord.getPath());
            detailModificationDate.setText(selectedFileRecord.getModificationDate());
            toolTipFileName.setText(selectedFileRecord.getName());
            toolTipFilePath.setText(selectedFileRecord.getPath());
            toolTipFileSize.setText(selectedFileRecord.getFileSize());
            toolTipModificationDate.setText(selectedFileRecord.getModificationDate());
            fileAccessorsListView.getItems().clear();
            fileAccessorsListView.getItems().addAll(selectedFile.getSharedAccounts());
        } catch (Exception e) {
            e.printStackTrace();
            // no need to handle
        }
    }

    @FXML
    void saveSelectedRecievedFile(ActionEvent event) {
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

    @FXML
    void showRecieverInfo(MouseEvent event){
        try {
            lblSenderEmail.setText(recievedFilesList.getSelectionModel().getSelectedItem().getSenderEmail());
            lblSenderEmail.visibleProperty().set(true);
            lblSender.visibleProperty().set(true);
        } catch (Exception e) {
        }
    }

    public void refreshRecievedFileList() {
        List<CustomRecievedFileListView> recievedSharedFiles = recievedFileOperations.getAll();
        if (recievedSharedFiles != null) {
            recievedFilesList.getItems().addAll(recievedSharedFiles);
        }
    }
}