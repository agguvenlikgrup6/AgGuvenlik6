package org.uludag.bmb.controller.scene;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import com.dropbox.core.json.JsonReader.FileLoadException;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.operations.DbxList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
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

    @FXML
    private SplitPane linkPane;

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
        TreeItem<String> root = DbxList.Hierarchy.getAsTreeItem("");
        treeView.setRoot(root);
        treeView.setShowRoot(false);
    }

    @FXML
    void listSelectFiles(MouseEvent event) {
    }

    @FXML
    void hierarchySelectFolder(MouseEvent event) {
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
            cloudListView.getItems().clear();
            cloudListView.getItems().addAll(DbxList.FILES(path));

            addToPathLink(pathNaked);
        }

    }

    private void addToPathLink(ArrayList<String> path) {
        linkPane.getItems().clear();

        Hyperlink pathPart = new Hyperlink("Dropbox/");
        pathPart.getStyleClass().add("pathPart");
        pathPart.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                cloudListView.getItems().clear();
                cloudListView.getItems().addAll(DbxList.FILES(""));
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
                    // Üstteki path kısmından seçilen klasöre uygun olarak treeview güncellenir
                    int selectedPathIndex = linkPane.getItems().indexOf(event.getSource());
                    int pathSize = linkPane.getItems().size();
                    for (int i = 0; i < pathSize - (selectedPathIndex + 1); i++) {
                        treeView.getSelectionModel().selectPrevious();
                    }
                    
                    //liste temizlenir ardından seçilen path'e göre güncellenir
                    cloudListView.getItems().clear();
                    String path = "/";
                    for (int i = 1; i <= linkPane.getItems().indexOf(event.getSource()); i++) {
                        path += ((Hyperlink) linkPane.getItems().get(i)).getText();
                    }
                    cloudListView.getItems().addAll(DbxList.FILES(path));
                    linkPane.getItems().remove(linkPane.getItems().indexOf(event.getSource()) + 1,
                            linkPane.getItems().size());
                }

            });
            linkPane.getItems().add(pathPart);
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