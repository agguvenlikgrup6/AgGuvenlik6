package org.uludag.bmb.beans.dataproperty;

import java.util.Date;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class CloudFileProperty {
    private final StringProperty fileName;
    private final ObjectProperty<Date> lastEditDate;
    private final StringProperty filePath;
    private final ObjectProperty<CheckBox> syncStatus;
    private final ObjectProperty<CheckBox> changeStatus;
    private final ObjectProperty<CheckBox> downloadStatus;

    public CloudFileProperty(int downloadStatus, String fileName, Date lastEditDate, String filePath,
            int syncStatus,
            int changeStatus) {
        this.downloadStatus = new SimpleObjectProperty<>(this, "downloadStatus",new CheckBoxWithStatus(downloadStatus));
        this.syncStatus = new SimpleObjectProperty<>(this, "syncStatus", new CheckBoxWithStatus(syncStatus));
        this.changeStatus = new SimpleObjectProperty<>(this, "changeStatus", new CheckBoxWithStatus(changeStatus));
        this.lastEditDate = new SimpleObjectProperty<>(this, "lastEditDate", lastEditDate);
        this.fileName = new SimpleStringProperty(this, "fileName", fileName);
        this.filePath = new SimpleStringProperty(this, "filepath", filePath);
    }

    public boolean getFileSyncStatus(){
        return this.syncStatus.get().selectedProperty().get();
    }

    public final ObjectProperty<Date> lastEditDate() {
        return lastEditDate;
    }

    public ObjectProperty<CheckBox> syncStatus() {
        return this.syncStatus;
    }

    public CheckBox getSyncStatus() {
        return this.syncStatus.get();
    }

    public void setSyncStatus(Boolean syncStatus) {
        this.syncStatus.get().selectedProperty().set(syncStatus);
    }

    public CheckBox getChangeStatus() {
        return this.changeStatus.get();
    }

    public void setChangeStatus(Boolean changeStatus) {
        this.changeStatus.get().selectedProperty().set(changeStatus);
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
        return filePath.get();
    }

    public final void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }
}
