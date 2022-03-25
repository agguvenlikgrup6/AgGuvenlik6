package org.uludag.bmb.entity.gui;

import org.uludag.bmb.entity.dropbox.DbClient;
import org.uludag.bmb.entity.gui.treeview.HieararchyTree;

import javafx.scene.control.TreeItem;

public abstract class FilePath {
    protected HieararchyTree hieararchy;
    protected TreeItem<String> treeItem;
    
    protected final DbClient client;
    
    public FilePath(DbClient client) {
        this.client = client;
        treeItem = new TreeItem<String>();
        setHieararchy();
    }

    public abstract void setHieararchy();
}
