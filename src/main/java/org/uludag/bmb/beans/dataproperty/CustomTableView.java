package org.uludag.bmb.beans.dataproperty;

import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class CustomTableView {
    private final StringProperty fileName;
    private final StringProperty lastEditDate;
    private final StringProperty filePath;
    private final ObjectProperty<CheckBox> syncStatus;
    private final ObjectProperty<CheckBox> changeStatus;
    private final ObjectProperty<CheckBox> downloadStatus;
    private final StringProperty fileSize;
    private final ObjectProperty<List<String>> sharedAccounts;
    private final StringProperty viewers;

    public CustomTableView(int downloadStatus, String fileName, String lastEditDate, String filePath, int syncStatus,
            int changeStatus, String fileSize, List<String> sharedAccounts, String viewers) {
        this.downloadStatus = new SimpleObjectProperty<>(this, "downloadStatus", new CustomCheckBox(downloadStatus));
        this.syncStatus = new SimpleObjectProperty<>(this, "syncStatus", new CustomCheckBox(syncStatus));
        this.changeStatus = new SimpleObjectProperty<>(this, "changeStatus", new CustomCheckBox(changeStatus));
        this.lastEditDate = new SimpleStringProperty(this, "lastEditDate", lastEditDate);
        this.fileName = new SimpleStringProperty(this, "fileName", fileName);
        this.filePath = new SimpleStringProperty(this, "filepath", filePath);
        this.fileSize = new SimpleStringProperty(this, "fileSize", fileSize);
        this.sharedAccounts = new SimpleObjectProperty<>(this, "sharedAccounts", sharedAccounts);
        int viewerCount = viewers.split(";").length;
        viewers = String.valueOf(viewerCount) + " kullanıcı";
        this.viewers = new SimpleStringProperty(this, "viewers", viewers);
    }

    public final ObjectProperty<List<String>> sharedAccounts() {
        return sharedAccounts;
    }

    public final List<String> getSharedAccounts() {
        return sharedAccounts.get();
    }

    public final void setSharedAccounts(List<String> sharedAccounts) {
        this.sharedAccounts.set(sharedAccounts);
    }

    public boolean getFileSyncStatus() {
        return this.syncStatus.get().selectedProperty().get();
    }

    public boolean hasFileChanged() {
        return this.changeStatus.get().selectedProperty().get();
    }

    public final StringProperty lastEditDate() {
        return lastEditDate;
    }

    public final StringProperty viewers(){
        return viewers;
    }

    public final String getViewers(){
        return this.viewers.get();
    }

    public final void setViewers(String viewers){
        this.viewers.set(viewers);
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

    public final String getLastEditDate() {
        return this.lastEditDate.get();
    }

    public final void setLastEditDate(String date) {
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

    public final StringProperty fileSize() {
        return fileSize;
    }

    public final String getFileSize() {
        return fileSize.get();
    }

    public void setFileSize(String fileSize) {
        this.fileSize.set(fileSize);
    }

}
