package org.uludag.bmb.beans.dataproperty;

import java.util.Date;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class TableViewDataProperty {
    private final StringProperty fileName;
    private final ObjectProperty<Date> lastEditDate;
    private final StringProperty filePath;
    private ObjectProperty<CheckBox> syncStatus;
    private final ObjectProperty<CheckBox> changeStatus;
    private final ObjectProperty<CheckBox> downloadStatus;

    public TableViewDataProperty(int downloadStatus, String fileName, Date lastEditDate, String filePath,
            int syncStatus,
            int changeStatus) {
        this.downloadStatus = new SimpleObjectProperty<>(this, "downloadStatus", new CheckBoxWithStatus(downloadStatus));
        this.lastEditDate = new SimpleObjectProperty<>(this, "lastEditDate", lastEditDate);
        this.fileName = new SimpleStringProperty(this, "fileName", fileName);
        this.syncStatus = new SimpleObjectProperty<>(this, "syncStatus", new CheckBoxWithStatus(syncStatus));
        this.filePath = new SimpleStringProperty(this, "filepath", filePath);
        this.changeStatus = new SimpleObjectProperty<>(this, "changeStatus", new CheckBoxWithStatus(changeStatus));
    }

    public final boolean getSync() {
        return this.syncStatus.get().selectedProperty().get();
    }

    public final ObjectProperty<Date> lastEditDate() {
        return lastEditDate;
    }

    public ObjectProperty<CheckBox> selection() {
        return this.syncStatus;
    }

    public boolean getChangeStatus() {
        return this.changeStatus.get().selectedProperty().get();
    }

    public void setChangeStatus(Boolean changeStatus) {
        this.changeStatus.get().selectedProperty().set(changeStatus);;
    }

    public ObjectProperty<CheckBox> changeStatus() {
        return this.changeStatus;
    }

    public CheckBox getDownloadStatus() {
        return this.downloadStatus.get();
    }

    public void setDownloadStatus(CheckBox downloadStatus) {
        this.downloadStatus.set(downloadStatus);
    }

    public ObjectProperty<CheckBox> downloadStatus() {
        return this.downloadStatus;
    }

    public CheckBox getSelection() {
        return this.syncStatus.get();
    }

    public void setSelection(CheckBox selection) {
        this.syncStatus.set(selection);
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
