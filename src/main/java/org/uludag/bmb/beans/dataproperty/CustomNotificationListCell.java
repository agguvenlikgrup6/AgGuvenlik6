package org.uludag.bmb.beans.dataproperty;

import javafx.scene.control.ListCell;

public class CustomNotificationListCell extends ListCell<String> {
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);

        } else {
            setMinWidth(this.getWidth());
            setMaxWidth(this.getWidth());
            setPrefWidth(this.getWidth());

            setWrapText(true);
            setText(item.toString());
        }
    }

}
