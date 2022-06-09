package org.uludag.bmb.controller.scene;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.constants.Constants;
import org.uludag.bmb.beans.database.FileRecord;
import org.uludag.bmb.beans.dataproperty.CustomHyperLink;
import org.uludag.bmb.beans.dataproperty.CustomNotificationListCell;
import org.uludag.bmb.beans.dataproperty.CustomRecievedFileListView;
import org.uludag.bmb.beans.dataproperty.CustomTableData;
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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
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
    public TableView<CustomTableData> fileListView;

    @FXML
    public Pane fileDetailPane;

    @FXML
    public TableColumn<CustomTableData, CheckBox> syncStatusColumn;

    @FXML
    public TableColumn<CustomTableData, String> filePathColumn;

    @FXML
    public TableColumn<CustomTableData, String> fileNameColumn;

    @FXML
    public TableColumn<CustomTableData, Date> modificationDateColumn;

    @FXML
    public TableColumn<CustomTableData, CheckBox> fileChangeStatusColumn;

    @FXML
    public TableColumn<CustomTableData, CheckBox> fileDownloadStatusColumn;

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
    public Label lbl1;

    @FXML
    public TableView<CustomRecievedFileListView> recievedFilesList;

    @FXML
    public TableColumn<CustomRecievedFileListView, String> recievedFileSenderEmail;

    @FXML
    public TableColumn<CustomRecievedFileListView, String> recievedFileDecryptedName;

    public MainSceneController() throws FileLoadException {
        super(PropertiesReader.getProperty("mainSceneFxml"), Integer.parseInt(PropertiesReader.getProperty("mainSceneWidth")), Integer.parseInt(PropertiesReader.getProperty("mainSceneHeigth")));
        refreshRecievedFileList();
    }

    @Override
    public void displayScene(Stage stage) {
        try {
            this.stage = stage;
            stage.setScene(scene);
            stage.hide();
            stage.setTitle("DROPBOX ŞİFRELİ DOSYA DEPOLAMA VE PAYLAŞMA UYGULAMASI");
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    directoriesHierarchyView.getSelectionModel().select(0);
                    directoriesHierarchyView.getSelectionModel().getSelectedItem().expandedProperty().set(true);
                    selectFolder();
                } catch (Exception e) {
                    // ilk açılışta ağaç görünümünde herhangi bir eleman olmayacağından
                    // exception handle
                }
            }
        });
        new NotificationPaneController(this);
    }

    @FXML
    void clearNotifications(MouseEvent event) {
        notificationListView.getItems().clear();
        notificationDot.visibleProperty().set(false);
        notificationPane.visibleProperty().set(false);
        lbl1.visibleProperty().set(true);
        fileIcon.visibleProperty().set(true);
    }

    @FXML
    void deleteSelectedFiles(ActionEvent event) {
        for (CustomTableData file : fileListView.getSelectionModel().getSelectedItems()) {
            FileOperations.deleteFile(file);
        }
    }

    @FXML
    void changeSyncStatusOn(ActionEvent event) {
        List<CustomTableData> selectedItems = fileListView.getSelectionModel().getSelectedItems();
        for (CustomTableData item : selectedItems) {
            item.syncStatus().get().selectedProperty().set(true);
            FileOperations.changeSyncStatus(item, true);
        }
    }

    @FXML
    void changeSyncStatusOff(ActionEvent event) {
        List<CustomTableData> selectedItems = fileListView.getSelectionModel().getSelectedItems();
        for (CustomTableData item : selectedItems) {
            item.syncStatus().get().selectedProperty().set(false);
            FileOperations.changeSyncStatus(item, false);
        }
    }

    @FXML
    void openFileDirectory(ActionEvent event) {
        String directoryPath = Constants.ACCOUNT.localSyncPath;
        String filePath = fileListView.getSelectionModel().getSelectedItem().getFilePath();
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.indexOf("mac") >= 0) {
                Runtime.getRuntime().exec("open -R " + directoryPath + filePath);
            } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
                Runtime.getRuntime().exec("xdg-open " + directoryPath + filePath);
            } else {
                filePath.replaceAll("/", "\\");
                Runtime.getRuntime().exec("explorer /select, " + directoryPath + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void hierarchySelectFolder(MouseEvent event) {
        selectFolder();
    }

    private void selectFolder() {
        fileIcon.getStyleClass().clear();
        fileIcon.getStyleClass().addAll("button", "iconFolder");
        TreeItem<String> selectedFolder = (TreeItem<String>) directoriesHierarchyView.getSelectionModel().getSelectedItem();
        try {
            StringBuilder folderPath = new StringBuilder();
            for (; selectedFolder.getParent() != null; selectedFolder = selectedFolder.getParent()) {
                folderPath.insert(0, selectedFolder.getValue() + "/");
            }
            ObservableList<CustomTableData> files = fileRecordOperations.getByPath(folderPath.toString());
            fileListView.setItems(files);
            fileListView.refresh();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    selectedDirectoryPathPane.getItems().clear();
                    List<String> selectedBarPath = Arrays.asList(folderPath.toString().split("/"));
                    if (selectedBarPath.size() == 0) {
                        selectedDirectoryPathPane.getItems().add(new CustomHyperLink(selectedDirectoryPathPane, directoriesHierarchyView, fileListView, ""));
                        return;
                    }
                    for (String pathPart : Arrays.asList(folderPath.toString().split("/"))) {
                        selectedDirectoryPathPane.getItems().add(new CustomHyperLink(selectedDirectoryPathPane, directoriesHierarchyView, fileListView, pathPart));
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
            lbl1.visibleProperty().set(false);
            fileIcon.visibleProperty().set(false);
        } else {
            notificationPane.visibleProperty().set(false);
            lbl1.visibleProperty().set(true);
            fileIcon.visibleProperty().set(true);
        }

    }

    @FXML
    void showSelectedFileDetails(MouseEvent event) {
        try {
            CustomTableData selectedFile = fileListView.getSelectionModel().getSelectedItem();
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
            FileRecord selectedFileRecord = fileRecordOperations.getByPathAndName(selectedFile.getFilePath(), selectedFile.getFileName());
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
    void showRecieverInfo(MouseEvent event) {
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
            recievedFilesList.getItems().clear();
            recievedFilesList.getItems().addAll(recievedSharedFiles);
        }
    }
}