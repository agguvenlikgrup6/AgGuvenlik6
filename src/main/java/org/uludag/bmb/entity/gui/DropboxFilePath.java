package org.uludag.bmb.entity.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dropbox.core.DbxException;
import com.dropbox.core.json.JsonReader.FileLoadException;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import org.uludag.bmb.entity.gui.treeview.HieararchyNode;
import org.uludag.bmb.entity.gui.treeview.HieararchyTree;

import javafx.scene.control.TreeItem;

public class DropboxFilePath extends FilePath implements PathTree {
    public DropboxFilePath() throws IOException, FileLoadException {
        super();
    }

    @Override
    public TreeItem<String> getTree() {
        return this.treeItem;
    }

    @Override
    public void setHieararchy() {
        readFolderPaths();
        this.hieararchy = new HieararchyTree(new HieararchyNode("/", ""));

        for (String data : this.folderPaths) {
            this.hieararchy.addElement(data);
        }

        setTreeItem(this.treeItem, this.hieararchy.root);
    }

    private void setTreeItem(TreeItem<String> rootItem, HieararchyNode hierarchyRoot) {
        int i = 0;
        for (HieararchyNode c : hierarchyRoot.childs) {
            hierarchyRoot = c;
            rootItem.getChildren().add(new TreeItem<>(hierarchyRoot.data));
            setTreeItem(rootItem.getChildren().get(i++), hierarchyRoot);
        }
    }

    @Override
    public void readFolderPaths() {
        this.folderPaths = new ArrayList<String>();
        try {
            ListFolderResult result = client.getClient().files().listFolderBuilder("")
                    .withIncludeDeleted(false)
                    .withRecursive(true)
                    .start();

            List<Metadata> entries = result.getEntries();

            for (Metadata metadata : entries) {
                if (metadata instanceof FolderMetadata) {
                    this.folderPaths.add(metadata.getPathDisplay());
                }
            }

        } catch (DbxException exception) {
            exception.printStackTrace();
        }
    }
}
