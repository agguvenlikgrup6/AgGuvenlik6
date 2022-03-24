package org.uludag.bmb.entity;

import java.util.ArrayList;
import java.util.List;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

public class PathHierarchy {
    
    private DbxCredential credential;

    public PathTree getHierarchy() {
        try {
            credential = DbxCredential.Reader.readFromFile("authinfo.json");
        } catch (JsonReader.FileLoadException e) {
            System.exit(1);
        }

        DbxRequestConfig requestConfig = new DbxRequestConfig("dbproject/1.0-SNAPSHOT");
        DbxClientV2 client = new DbxClientV2(requestConfig, credential);

        try {
            ListFolderResult result = client.files().listFolderBuilder("")
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
