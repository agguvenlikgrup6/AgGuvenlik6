package org.uludag.bmb.controller.scene.listview;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.uludag.bmb.controller.scene.Controller;

public class ListViewController extends Controller {
    public ListViewController() {
        super("listview", 400, 500);
        setListView();
    }

    @FXML
    private ListView listView;

    ObservableList observableList = FXCollections.observableArrayList();
    private List<String> stringList = new ArrayList<>(5);

    public void setListView() {
        stringList.add("String 1");
        stringList.add("String 2");
        stringList.add("String 3");
        stringList.add("String 4");
        observableList.setAll(stringList);
        listView.setItems(observableList);
        
        listView.setCellFactory(new Callback<ListView<String>, javafx.scene.control.ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> listView) {
                return new ListViewCell();
            }
        });
    }

    @Override
    public void displayScene(Stage stage) {
        this.stage = stage;
        stage.setScene(scene);
        stage.hide();
        stage.show();
    }
}