package org.uludag.bmb.controller.scene;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import com.dropbox.core.DbxException;
import com.dropbox.core.json.JsonReader.FileLoadException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.entity.dropbox.DbClient;
import org.uludag.bmb.entity.scene.DropboxFilePath;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MainSceneController extends Controller implements Initializable {
    @FXML
    private Button btnDownload;

    @FXML
    private Button btnUpload;

    @FXML
    private ListView<String> cloudListView;

    @FXML
    private ListView<String> localListView;

    @FXML
    private TabPane tabPane;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private Font x1;

    public MainSceneController() throws FileLoadException {
        super(PropertiesReader.getProperty("mainSceneFxml"),
                Integer.parseInt(PropertiesReader.getProperty("mainSceneWidth")),
                Integer.parseInt(PropertiesReader.getProperty("mainSceneHeigth")));
    }

    @Override
    public void displayScene(Stage stage) {
        this.stage = stage;
        stage.setScene(scene);
        scene.getStylesheets().add(PropertiesReader.getProperty("mainSceneCss"));
        stage.hide();
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DropboxFilePath paths = null;
        try {
            paths = new DropboxFilePath();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FileLoadException e) {
            e.printStackTrace();
        }
        TreeItem<String> tree = paths.getTree();
        treeView.setRoot(tree);
    }

    @FXML
    void listSelectFiles(MouseEvent event) {
        ArrayList<String> selectedFiles = new ArrayList<String>();
        cloudListView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) -> System.out.println(
                        "Check box for " + item + " changed from " + wasSelected + " to " + isNowSelected));

                return observable;
            }
        }));
    }

    @FXML
    void hierarchySelectFolder(MouseEvent event) {
        ArrayList<String> path = new ArrayList<String>();
        TreeItem<String> selectedFolder = (TreeItem<String>) treeView.getSelectionModel().getSelectedItem();
        var item = selectedFolder;

        if (item != null) {
            while (item.getParent() != null) {
                path.add(item.getValue() + "/");
                item = item.getParent();
            }
            path.add("/");
            Collections.reverse(path);

            DbClient client = new DbClient(true);

            ListFolderResult result;
            try {
                result = client.getClient().files().listFolder(String.join("", path));
                List<Metadata> entries = result.getEntries();
                cloudListView.getItems().clear();
                for (Metadata metadata : entries) {
                    if (metadata instanceof FileMetadata) {
                        cloudListView.getItems().add(metadata.getName());
                    }
                }
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void downloadItem(ActionEvent event) {

    }

    @FXML
    void uploadItem(ActionEvent event) throws IOException {

    }

    @FXML
    void getPath(MouseEvent event) throws IOException {

    }
}