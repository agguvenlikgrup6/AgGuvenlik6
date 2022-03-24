package org.uludag.bmb.entity.gui;

import java.util.ArrayList;
import java.util.List;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import org.uludag.bmb.entity.dropbox.DbClient;

import javafx.scene.control.TreeItem;

public class DropboxFilePath extends FilePath implements PathTree {

    public DropboxFilePath(DbClient client) {
        super(client);
    }

    @Override
    public TreeItem<String> getTree() {
        return treeItem;
    }

    @Override
    public void setHieararchy() {
        try {
            ListFolderResult result = client.getClient().files().listFolderBuilder("")
                    .withIncludeDeleted(false)
                    .withRecursive(true)
                    .start();

            List<Metadata> entries = result.getEntries();
            ArrayList<String> slist = new ArrayList<>();

            for (Metadata metadata : entries) {
                if (metadata instanceof FolderMetadata) {
                    slist.add(metadata.getPathDisplay());
                }
            }

            hieararchy = new HieararchyTree(new HieararchyNode("/", ""));

            for (String data : slist) {
                hieararchy.addElement(data);
            }

        } catch (DbxException exception) {
            System.err.println(exception.getMessage());
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
}
