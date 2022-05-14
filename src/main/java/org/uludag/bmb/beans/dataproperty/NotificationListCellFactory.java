package org.uludag.bmb.beans.dataproperty;

import javafx.scene.control.ListCell;

public class NotificationListCellFactory extends ListCell<String> {
    // list view hücrelerinin yana doğru genişlemesini engellemek için
    // text wrapper kullanmak adına cell factory oluşturuldu.
    // css üzerinden yapılamıyor...
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
