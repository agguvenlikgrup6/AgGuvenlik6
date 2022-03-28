package org.uludag.bmb.entity.scene;

import java.io.IOException;

import com.dropbox.core.json.JsonReader.FileLoadException;

import javafx.scene.control.TreeItem;

public class LocalFilePath extends FilePath implements PathTree{
    public LocalFilePath() throws IOException, FileLoadException {
        super();
    }

    @Override
    public TreeItem<String> getTree() {
        return treeItem;
    }
    
    @Override
    public void setHieararchy() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void readFolderPaths() {
        // TODO Auto-generated method stub
        
    }
}
