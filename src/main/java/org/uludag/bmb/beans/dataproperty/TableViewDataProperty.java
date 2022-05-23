package org.uludag.bmb.beans.dataproperty;

import java.util.Date;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class TableViewDataProperty {
    private final StringProperty fileName;
    private final ObjectProperty<Date> lastEditDate;
    private final StringProperty filePath;
    private ObjectProperty<CheckBox> selection;
    private final ObjectProperty<CheckBox> changeStatus;

    public TableViewDataProperty(String fileName, Date lastEditDate, String filePath, int sync, int changeStatus) {
        this.lastEditDate = new SimpleObjectProperty<>(this, "lastEditDate", lastEditDate);
        this.fileName = new SimpleStringProperty(this, "fileName", fileName);
        CheckBox cb = new CheckBox();
        cb.disableProperty().set(true);
        if (sync == 1) {
            cb.selectedProperty().set(true);
        } else {
            cb.selectedProperty().set(false);
        }
        this.selection = new SimpleObjectProperty<>(this, "selection", cb);
        this.filePath = new SimpleStringProperty(this, "filepath", filePath);

        CheckBox cb1 = new CheckBox();
        cb1.disableProperty().set(true);
        if (changeStatus == 1) {
            cb1.selectedProperty().set(true);
        } else {
            cb1.selectedProperty().set(false);
        }
        this.changeStatus = new SimpleObjectProperty<>(this, "changeStatus", cb1);
    }

    public final boolean getSync() {
        return this.selection.get().selectedProperty().get();
    }

    public final ObjectProperty<Date> lastEditDate() {
        return lastEditDate;
    }

    public ObjectProperty<CheckBox> selection() {
        return this.selection;
    }

    public CheckBox getChangeStatus() {
        return this.changeStatus.get();
    }

    public void setChangeStatus(CheckBox changeStatus) {
        this.changeStatus.set(changeStatus);
    }

    public ObjectProperty<CheckBox> changeStatus() {
        return this.changeStatus;
    }

    public CheckBox getSelection() {
        return this.selection.get();
    }

    public void setSelection(CheckBox selection) {
        this.selection.set(selection);
    }

    public final Date getLastEditDate() {
        return this.lastEditDate.get();
    }

    public final void setLastEditDate(Date date) {
        this.lastEditDate.set(date);
    }

    public final StringProperty fileName() {
        return fileName;
    }

    public final String getFileName() {
        return fileName.get();
    }

    public final void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public final StringProperty filePath() {
        return filePath;
    }

    public final String getFilePath() {
        String p = filePath.get();
        return p;
    }

    public final void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }
}
