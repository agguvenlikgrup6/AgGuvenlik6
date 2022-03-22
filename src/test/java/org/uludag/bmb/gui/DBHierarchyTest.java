package org.uludag.bmb.gui;

import org.junit.Test;

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

// public class DBHierarchyTest {

//     @Test
//     public void listFolderAndFiles() {
//         DbxCredential credential;
//         try {
//             credential = DbxCredential.Reader.readFromFile("authinfo.json");
//         } catch (JsonReader.FileLoadException e) {
//             return;
//         }

//         DbxRequestConfig requestConfig = new DbxRequestConfig("dbproject/1.0-SNAPSHOT");
//         DbxClientV2 client = new DbxClientV2(requestConfig, credential);

//         try {
//             ListFolderResult result = client.files().listFolderBuilder("")
//                     .withIncludeDeleted(false)
//                     .withRecursive(true)
//                     .start();

//             List<Metadata> entries = result.getEntries();
//             ArrayList<String> slist = new ArrayList<>();

//             for (Metadata metadata : entries) {
//                 if (metadata instanceof FolderMetadata) {
//                     slist.add(metadata.getPathDisplay());
//                 }
//             }

//             MXMTree tree = new MXMTree(new MXMNode("root", ""));

//             for (String data : slist) {
//                 tree.addElement(data);
//             }

//             tree.printTree();

//         } catch (DbxException exception) {
//             System.err.println(exception.getMessage());
//         }

//     }
// }
