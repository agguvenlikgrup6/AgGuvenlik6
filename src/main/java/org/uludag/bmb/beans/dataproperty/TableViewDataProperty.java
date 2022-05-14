package org.uludag.bmb.beans.dataproperty;

import java.util.Date;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableViewDataProperty {
    private final StringProperty fileName;
    private final ObjectProperty<Date> lastEditDate;
    private final BooleanProperty syncStatus;
    private final StringProperty filePath;

    public TableViewDataProperty(String fileName, Date lastEditDate, boolean syncStatus, String filePath) {
        this.lastEditDate = new SimpleObjectProperty<>(this, "lastEditDate", lastEditDate);
        this.fileName = new SimpleStringProperty(this, "fileName", fileName);
        this.syncStatus = new SimpleBooleanProperty(this, "syncStatus", syncStatus);
        this.filePath = new SimpleStringProperty(this, "filepath", filePath);
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
        return fileName;
    }

    public final String getFileName() {
        return fileName.get();
    }

    public final void setFileName(String fileName) {
        this.fileName.set(fileName);
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
