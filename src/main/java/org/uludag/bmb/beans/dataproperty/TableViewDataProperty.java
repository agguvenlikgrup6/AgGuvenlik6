package org.uludag.bmb.beans.dataproperty;

import java.util.Date;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;

public class TableViewDataProperty {
    private final StringProperty fileName1;
    private final ObjectProperty<Date> lastEditDate;
    private final BooleanProperty syncStatus;
    private ObjectProperty<CheckBox> selection;
    private final StringProperty filePath;

    public TableViewDataProperty(String fileName, Date lastEditDate, boolean syncStatus, String filePath) {
        this.lastEditDate = new SimpleObjectProperty<>(this, "lastEditDate", lastEditDate);
        this.fileName1 = new SimpleStringProperty(this, "fileName", fileName);
        this.syncStatus = new SimpleBooleanProperty(this, "syncStatus", syncStatus);
        this.selection = new SimpleObjectProperty<>(this, "selection", new CheckBox());
        this.filePath = new SimpleStringProperty(this, "filepath", filePath);
    }

    public void addEventHandler(EventHandler<MouseEvent> a) {
        this.selection.get().addEventHandler(MouseEvent.MOUSE_CLICKED, a);
    }

    public ObjectProperty<CheckBox> selection() {
        return this.selection;
    }

    public CheckBox getSelection() {
        return this.selection.get();
    }

    public void setSelection(CheckBox selection) {
        this.selection.set(selection);
    }

    public final ObjectProperty<Date> lastEditDate() {
        return lastEditDate;
    }

    public final Date getLastEditDate() {
        return this.lastEditDate.get();
    }

    public final void setLastEditDate(Date date) {
        this.lastEditDate.set(date);
    }

    public final StringProperty fileName() {
        return fileName1;
    }

    public final String getFileName() {
        return fileName1.get();
    }

    public final void setFileName(String fileName) {
        this.fileName1.set(fileName);
    }

    public final BooleanProperty syncStatus() {
        return syncStatus;
    }

    public final Boolean getSyncStatus() {
        return syncStatus.get();
    }

    public final void setSyncStatus(Boolean syncStatus) {
        this.syncStatus.set(syncStatus);
    }

    public final StringProperty filePath() {
        return filePath;
    }

    public final String getFilePath() {
        String p = filePath.get();
        int index = p.lastIndexOf('/');
        return p.substring(1, index);
    }

    public final void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }
}
