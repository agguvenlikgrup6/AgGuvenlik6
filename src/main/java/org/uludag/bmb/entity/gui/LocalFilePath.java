package org.uludag.bmb.entity.gui;

import org.uludag.bmb.entity.dropbox.DbClient;

import javafx.scene.control.TreeItem;

public class LocalFilePath extends FilePath implements PathTree{

    public LocalFilePath(DbClient client) {
        super(client);
    }

    @Override
    public TreeItem<String> getTree() {
        return treeItem;
    }
    
    @Override
    public void setHieararchy() {
        // TODO Auto-generated method stub
        
    }
}
