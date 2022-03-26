package org.uludag.bmb.entity.gui;

import javafx.scene.control.TreeItem;

public class LocalFilePath extends FilePath implements PathTree{
    @Override
    public TreeItem<String> getTree() {
        return treeItem;
    }
    
    @Override
    public void setHieararchy() {
        // TODO Auto-generated method stub
        
    }
}
