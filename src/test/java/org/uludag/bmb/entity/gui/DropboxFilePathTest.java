package org.uludag.bmb.entity.gui;

import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.dropbox.core.DbxException;
import com.dropbox.core.json.JsonReader.FileLoadException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import org.junit.Test;
import org.uludag.bmb.beans.dataproperty.TableViewDataProperty;
import org.uludag.bmb.controller.database.DatabaseController;
import org.uludag.bmb.operations.dropbox.Client;

public class DropboxFilePathTest {

    @Test
    public void listDropboxFolderPaths() throws IOException, FileLoadException, ListFolderErrorException, DbxException {
        ListFolderResult result = Client.client.files().listFolderBuilder("")
                .withIncludeDeleted(false)
                .withRecursive(true)
                .start();

        List<Metadata> entries = result.getEntries();

        for (Metadata metadata : entries) {
            if (metadata instanceof FileMetadata) {
                System.out.println(metadata.getPathLower());
            }
        }
    }

    @Test
    public void getAllFiles() throws IOException, FileLoadException, ListFolderErrorException, DbxException {
        ListFolderResult result = Client.client.files().listFolderBuilder("")
                .withIncludeDeleted(false)
                .withRecursive(true)
                .start();

        List<Metadata> entries = result.getEntries();

        for (Metadata metadata : entries) {
            if (metadata instanceof FileMetadata) {
                String fileName = metadata.getName();
                FileMetadata fileMetadata = (FileMetadata) Client.client.files()
                        .getMetadata(metadata.getPathLower());
                String filePath = fileMetadata.getPathDisplay();
                filePath = filePath.substring(0, filePath.length() - fileName.length());
                Date fileDate = fileMetadata.getServerModified();
                DatabaseController dc = new DatabaseController();
                dc.insertTreeCache(new TableViewDataProperty(fileName, fileDate, false, filePath));
                System.out.println(filePath + " -- " + fileName + " -- " + fileDate);
            }
        }
    }

}
