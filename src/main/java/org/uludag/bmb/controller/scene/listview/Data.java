package org.uludag.bmb.controller.scene.listview;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import java.io.IOException;

public class Data
{
    @FXML
    private HBox hBox;
    @FXML
    private Label label1;

    public Data()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Data.class.getResource("/" + "listCellItem" + ".fxml"));
        fxmlLoader.setController(this);
        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void test123(MouseEvent event) {
        System.out.println("hmmmm");
    }

    public void setInfo(String string)
    {
        label1.setText(string);
    }

    public HBox getBox()
    {
        return hBox;
    }
}