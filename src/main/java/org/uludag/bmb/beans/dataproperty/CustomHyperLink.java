package org.uludag.bmb.beans.dataproperty;

import java.util.List;

import org.uludag.bmb.operations.database.FileRecordOperations;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

public class CustomHyperLink extends Hyperlink {
    private static FileRecordOperations fileRecordOperations = new FileRecordOperations();
    private int index;

    public CustomHyperLink(SplitPane pathBar, TableView<CustomTableView> cloudTableView, String text) {
        if(text.equals("")){
            text = "Dropbox";
        }
        super.setText(text);
        super.getStyleClass().add("pathPart");

        List<Node> splitPaneItems = pathBar.getItems();
        StringBuilder path = new StringBuilder();
        index = pathBar.getItems().size() + 1;
        if (splitPaneItems.size() != 0) {
            for (int i = 0; i < splitPaneItems.size(); i++) {
                if (i == 0) {
                    path.append("/");
                } else {
                    CustomHyperLink styledBarPart = (CustomHyperLink) splitPaneItems.get(i);
                    path.append(styledBarPart.getText() + "/");
                }
            }
            path.append(text + "/");
        }

        this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ObservableList<CustomTableView> files;
                if (path.toString().equals(""))
                    files = fileRecordOperations.getByPath("/");
                else
                    files = fileRecordOperations.getByPath(path.toString());
                cloudTableView.setItems(files);
                pathBar.getItems().remove(index, pathBar.getItems().size());
            }
        });

        this.setText(text + "/");
    }
}
