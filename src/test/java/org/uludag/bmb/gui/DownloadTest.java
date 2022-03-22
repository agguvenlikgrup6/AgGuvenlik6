package org.uludag.bmb.gui;

// import org.junit.Test;

// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.util.List;

// import com.dropbox.core.DbxDownloader;
// import com.dropbox.core.DbxException;
// import com.dropbox.core.DbxRequestConfig;
// import com.dropbox.core.json.JsonReader;
// import com.dropbox.core.oauth.DbxCredential;
// import com.dropbox.core.v2.DbxClientV2;
// import com.dropbox.core.v2.files.FileMetadata;
// import com.dropbox.core.v2.files.FolderMetadata;
// import com.dropbox.core.v2.files.ListFolderResult;
// import com.dropbox.core.v2.files.Metadata;

// public class DownloadTest {
//     @Test
//     public void downloadFile() throws IOException{
//         DbxCredential credential;
//         try {
//             credential = DbxCredential.Reader.readFromFile("authinfo.json");
//         } catch (JsonReader.FileLoadException e) {
//             return;
//         }

//         DbxRequestConfig requestConfig = new DbxRequestConfig("dbproject/1.0-SNAPSHOT");
//         DbxClientV2 client = new DbxClientV2(requestConfig, credential);

//         try {
//             DbxDownloader<FileMetadata> downloader = client.files().download("/Test_1/test2.txt");
//             try {
//                 FileOutputStream out = new FileOutputStream("C:/Users/batuh/OneDrive/Masaüstü/xxx/testa.txt");
//                 downloader.download(out);
//                 out.close();          
//             } catch (DbxException ex) {
//                 System.out.println(ex.getMessage());
//             }
//         } catch (DbxException exception) {
//             System.err.println(exception.getMessage());
//         }

//     }
// }
