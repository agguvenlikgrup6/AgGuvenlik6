package org.uludag.bmb.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class mainSceneController implements Initializable{

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        TreeItem<String> rootItem = new TreeItem<>("Files");
        
        TreeItem<String> branchItem1 = new TreeItem<>("aaa");
        TreeItem<String> branchItem2 = new TreeItem<>("bbb");
        TreeItem<String> branchItem3 = new TreeItem<>("ccc");


        TreeItem<String> leafItem1 = new TreeItem<>("aa");
        TreeItem<String> leafItem2 = new TreeItem<>("bb");
        TreeItem<String> leafItem3 = new TreeItem<>("cc");
        
        branchItem1.getChildren().add(leafItem1);
        branchItem2.getChildren().add(leafItem2);
        branchItem3.getChildren().add(leafItem3);
       
        rootItem.getChildren().addAll(branchItem1,branchItem2,branchItem3);

        treeView.setRoot(rootItem);

    }
        
    @FXML
    private Button btnDownload;

    @FXML
    private Button btnUpload;

    @FXML
    private TreeView treeView;

    @FXML
    private Font x1;

    @FXML
    private Color x2;

    @FXML
    private Font x3;

    @FXML
    private Color x4;

    @FXML
    void selectItem(MouseEvent event) {
        TreeItem<String> item =(TreeItem<String>) treeView.getSelectionModel().getSelectedItem();
        if(item.getValue()!=null){
            System.out.println(item.getValue());
        }
    }

}
