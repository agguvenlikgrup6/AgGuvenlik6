package org.uludag.bmb.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import javafx.event.ActionEvent;
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
import javafx.scene.text.Text;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

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
    private Text files;

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
    @FXML
    void downloadItem(ActionEvent event) {
        JFileChooser chooser ;
        
        chooser = new JFileChooser(); 
        chooser.showSaveDialog(null);
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("choosertitle");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        System.out.println(chooser.getCurrentDirectory());
        // JFrame frame = new JFrame("");
        // DemoJFileChooser panel = new DemoJFileChooser();
        // frame.addWindowListener(
        //   new WindowAdapter() {
        //     public void windowClosing(WindowEvent e) {
        //       System.exit(0);
        //       }
        //     }
        //   );
        // frame.getContentPane().add(panel,"Center");
        // frame.setSize(panel.getPreferredSize());
        // frame.setVisible(true);
        // System.out.println("aasas");
        // Download download=new Download();

    }

   

    @FXML
    void uploadItem(ActionEvent event) throws IOException {
        JFileChooser fileChooser=new JFileChooser();
        fileChooser.showOpenDialog(null);
        
        System.out.println(fileChooser.getSelectedFile().toPath().toString());
        String path=fileChooser.getSelectedFile().toPath().toString();
        UploadFile uploadFile=new UploadFile();
        uploadFile.uploadFileFunc(path);
    }
    
    @FXML
    void getPath(MouseEvent event) throws IOException {
        System.out.println(files.getText());
       
        // download.downloadFile(files.getText());
    }

}
