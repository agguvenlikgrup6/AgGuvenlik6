package org.uludag.bmb.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Alert alert = new Alert(AlertType.CONFIRMATION);
        // alert.setTitle("Bilgilendirme");
        // ButtonType type = new ButtonType("Tamam", ButtonData.OK_DONE);
        // alert.getDialogPane().getButtonTypes().add(type);
        // alert.getDialogPane().getButtonTypes().remove(0);
        // alert.getDialogPane().getButtonTypes().remove(0);
        // alert.setHeaderText("Lütfen Dropbox Dosyalarının Depolanacağı Konumu Seçiniz");
        // alert.setContentText("Tamama basınca açılan pencerden depolama dizinini seçiniz");
        // alert.showAndWait();

        // DirectoryChooser directoryChooser = new DirectoryChooser();
        // directoryChooser.setTitle("Dropbox Depolama Dizinini Seçiniz");
        // directoryChooser.setInitialDirectory(null);

        // try {
        //     var path = directoryChooser.showDialog(null).getAbsolutePath();
        // } catch (NullPointerException e) {

        // }
        // TODO path seçilen path bilgisini tutar, bu bilgi ile depolama dizini oluşturulmalı

        scene = new Scene(loadFXML("startupScene"));
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}