package org.uludag.bmb.controller.scene;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.sharing.MemberSelector;

import org.uludag.bmb.beans.dataproperty.TableViewDataProperty;
import org.uludag.bmb.operations.dropbox.Client;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ShareWindowController implements Initializable {
    private ObservableList<TableViewDataProperty> fileList;

    @FXML
    private Button btnCreateLink;

    @FXML
    private Button btnShareWithMails;

    @FXML
    private SplitPane fileNamePane;

    @FXML
    private TextArea emailList;

    @FXML
    void createLink(ActionEvent event) {
        
    }

    @FXML
    void shareWithMails(ActionEvent event) {
        List<MemberSelector> members = new ArrayList<>();
        var mails = emailList.getText().split(";");
        for (String mail : mails) {
            members.add(MemberSelector.email(mail));
        }
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Dosya Paylaşım");
        try {
            for (TableViewDataProperty f : fileList) {
                Client.client.sharing().addFileMember(f.getFilePath() + f.getFileName(), members);
            }
            alert.setHeaderText("Dosya Paylaşımı Başarı İle Sonuçlandı.\nPaylaşılan Hesaplar:");
            alert.setContentText(emailList.getText());
            alert.showAndWait();

            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();

        } catch (DbxException e) {
            alert.setHeaderText("Dosya Paylaşımı Başarısızlıkla Sonuçlandı");
            alert.showAndWait();
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
            e.printStackTrace();
        }
    }

    private void setFiles(ObservableList<TableViewDataProperty> fileList) {
        fileNamePane.getItems().clear();
        for (TableViewDataProperty file : fileList) {
            fileNamePane.getItems().add(new Label(file.getFileName()));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setFileList(ObservableList<TableViewDataProperty> fileList) {
        this.fileList = fileList;
        this.setFiles(fileList);
    }
}
