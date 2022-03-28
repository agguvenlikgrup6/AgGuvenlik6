package org.uludag.bmb.controller.scene;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;


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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MainSceneController extends Controller implements Initializable {
    String path;

    @FXML
    private Button btnDownload;

    @FXML
    private Button btnUpload;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private Text files;
    @FXML 
    private ListView<String> cloudListView;

    public MainSceneController() throws IOException, FileLoadException{
        try {
            fxmlLoad(PropertiesReader.getProperty("mainSceneFxml"),
                    Integer.parseInt(PropertiesReader.getProperty("mainSceneWidth")),
                    Integer.parseInt(PropertiesReader.getProperty("mainSceneHeigth")));
        } catch (NumberFormatException | IOException e) {
            System.err.println(e.getMessage());
        }
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
            DbClient client =new DbClient();
            client.login();
            ListFolderResult result = client.getClient().files().listFolder(path);

            List<Metadata> entries = result.getEntries();
            
            
            cloudListView.getItems().clear();
            for (Metadata metadata : entries) {
                if (metadata instanceof FileMetadata) {
                        
                    cloudListView.getItems().add(metadata.getName());
                 
                }
            }
            System.out.println(123);
            cloudListView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue<Boolean> call(String item) {
                    BooleanProperty observable = new SimpleBooleanProperty();
                    observable.addListener((obs, wasSelected, isNowSelected) -> 
                        System.out.println("Check box for "+item+" changed from "+wasSelected+" to "+isNowSelected));
                        

                    return observable ;
                }
            }));
          
       

        } catch (Exception e) {

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