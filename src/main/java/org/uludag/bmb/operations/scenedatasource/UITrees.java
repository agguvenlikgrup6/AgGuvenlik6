package org.uludag.bmb.operations.scenedatasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import org.uludag.bmb.operations.dropbox.Client;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UITrees {
    public final class Hierarchy {
        public static final List<String> toList(String path) {
            List<String> folders = new ArrayList<String>();
            ListFolderResult result;
            try {
                result = Client.client.files().listFolderBuilder(path)
                        .withIncludeDeleted(false)
                        .withRecursive(true)
                        .start();

                List<Metadata> entries = result.getEntries();
                for (Metadata metadata : entries) {
                    if (!(metadata instanceof FileMetadata)) {
                        folders.add(metadata.getPathDisplay());
                    }
                }
            } catch (DbxException e) {
                e.printStackTrace();
            }
            return folders;
        }

        public static final TreeItem<String> getAsTreeItem(String path) {
            TreeItem<String> root = new TreeItem<>();
            Map<String, TreeItem<String>> items = new HashMap<>();
            Image nodeImage = new Image(UITrees.class.getResourceAsStream("/folder.png"));
            for (String p : toList(path)) {
                getItem(items, root, p, nodeImage);
            }
            return root;
        }

        private static TreeItem<String> getItem(Map<String, TreeItem<String>> items, TreeItem<String> root,
                String itemPath, Image nodeImage) {
            TreeItem<String> result = items.get(itemPath);

            if (result == null) {
                int index = itemPath.lastIndexOf('/');
                result = new TreeItem<>(itemPath.substring(index + 1), new ImageView(nodeImage));
                items.put(itemPath, result);

                if (index == -1) {
                    root.getChildren().add(result);
                } else {
                    TreeItem<String> parent = getItem(items, root, itemPath.substring(0, index), nodeImage);
                    parent.getChildren().add(result);
                }
            }
            return result;
        }
    }
}
