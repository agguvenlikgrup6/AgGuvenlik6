package org.uludag.bmb;

import java.net.URISyntaxException;
import java.io.File;
import java.io.IOException;

import org.uludag.bmb.Company.FileSystem;
import org.uludag.bmb.Company.FileSystemRSA;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        try{
            //FileSystem tt = new FileSystem();
            //tt.dosyaSifreleme("deneme.txt", "aa.txt");
            //tt.dosyaSifreCozme("aa.txt","dd.txt");
            FileSystemRSA tt_rsa = new FileSystemRSA();
            //tt_rsa.dosyaSifreleme("deneme.txt", "aa.txt");
            tt_rsa.dosyaSifreCozme("aa.txt", "bb.txt");

        }
        catch(Exception ignored){
            
        }
    }
}
