package org.uludag.bmb;

import java.net.URISyntaxException;
import java.io.IOException;

import org.uludag.bmb.Company.Encrypt;
import org.uludag.bmb.Company.EncryptNew;
import org.uludag.bmb.Company.FileSystem;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        try{
            FileSystem tt = new FileSystem();
            EncryptNew aa = new EncryptNew();
            tt.dosyaSifreleme("deneme.txt", "aa.txt");
            tt.dosyaSifreCozme("aa.txt","dd.txt");
        }
        catch(Exception ignored){
            
        }
    }
}
