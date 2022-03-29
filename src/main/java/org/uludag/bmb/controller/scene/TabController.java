package org.uludag.bmb.controller.scene;


import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

public class TabController {
    @FXML
    private TabPane tabPane;


    public TabController()  {
        
    }

    @FXML
    public void testEvent(MouseEvent event){
        System.out.println("AAAAAAAAAAAAAAAAAAAA");
    } 

    
}
