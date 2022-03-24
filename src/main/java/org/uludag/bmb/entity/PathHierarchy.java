package org.uludag.bmb.entity;

import java.util.ArrayList;
import java.util.List;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.uludag.bmb.operations.dropbox.DbClient;
import org.uludag.bmb.operations.dropbox.DbModule;

public class PathHierarchy {

    private DbClient client;
    private Injector injector;

    public PathHierarchy() {
        injector = Guice.createInjector(new DbModule());
        this.client = injector.getInstance(DbClient.class);
    }

    public PathTree getHierarchy() {
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

            PathTree tree = new PathTree(new PathNode("/", ""));

            for (String data : slist) {
                tree.addElement(data);
            }

            return tree;

        } catch (DbxException exception) {
            System.err.println(exception.getMessage());
            System.exit(1);
            return null;
        }

    }
}
