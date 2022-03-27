package org.uludag.bmb.controller.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import com.dropbox.core.json.JsonReader.FileLoadException;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.entity.gui.DropboxFilePath;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainSceneController extends Controller implements Initializable {
    String path;

    @FXML
    private Button btnDownload;

    @FXML
    private ListView<String> fileList;

    @FXML
    private Button btnUpload;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private Text files;

    public MainSceneController() throws IOException, FileLoadException{
        try {
            fxmlLoad(PropertiesReader.getProperty("mainSceneFxml"),
                    Integer.parseInt(PropertiesReader.getProperty("mainSceneWidth")),
                    Integer.parseInt(PropertiesReader.getProperty("mainSceneHeigth")));
        } catch (NumberFormatException | IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void displayHomeScreen(Stage stage) {
        this.stage = stage;
        stage.setScene(scene);
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

            // ListFolderResult result = client.getClient().files().listFolder(path);

            // List<Metadata> entries = result.getEntries();

            TreeItem<String> treeitem = new TreeItem<>("AAAA");
            // showFiles.setRoot(treeitem);

            // for (Metadata metadata : entries) {
            //     if (metadata instanceof FileMetadata) {
            //         treeitem.getChildren().add(new TreeItem<>(metadata.getName()));
            //     }
            // }

        } catch (Exception e) {

        }
    }

    @FXML
    void downloadItem(ActionEvent event) {
    }

    @FXML
    void uploadItem(ActionEvent event) throws IOException {
        // try {
        // try (InputStream in = new FileInputStream(uploadFilePath)) {
        // client.getClient().files().uploadBuilder(path +
        // fileName).uploadAndFinish(in);
        // }
        // } catch (DbxException exception) {
        // System.err.println(exception.getMessage());
        // }
    }

    @FXML
    void getPath(MouseEvent event) throws IOException {

    }
}