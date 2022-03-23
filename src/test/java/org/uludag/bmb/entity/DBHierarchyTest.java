package org.uludag.bmb.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import org.junit.Test;

public class DBHierarchyTest {
    private DbxCredential credential;

    @Test
    public void testGetHierarchy() {
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
            slist = new ArrayList<>();
            slist.add("/A/B/A");
            slist.add("/A/A/A_1");

            Collections.sort(slist, new LengthFirstComparator());
            System.out.println("");


        } catch (DbxException exception) {
            System.err.println(exception.getMessage());
            System.exit(1);
        }
    }

    public class LengthFirstComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {         
            long o1PathCount = o1.chars().filter(ch -> ch == '/').count();
            long o2PathCount = o2.chars().filter(ch -> ch == '/').count();

            if(o1PathCount == o2PathCount)
                return o1.compareTo(o2);
            else{
                if(o1PathCount < o2PathCount)
                    return -1;
                else
                    return +1;
            }
        }
    }
}
