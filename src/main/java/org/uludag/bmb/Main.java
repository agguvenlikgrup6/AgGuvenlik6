package org.uludag.bmb;

import java.net.URISyntaxException;
import java.io.File;
import java.io.IOException;

import org.uludag.bmb.Company.FileSystem;
import org.uludag.bmb.Company.FileSystemRSA;
import org.uludag.bmb.Company.TryRSA;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        try{
            FileSystem tt = new FileSystem();
            //tt.dosyaSifrelemeAll("deneme.txt");
            //tt.dosyaAdSifreCozme("jXz9sm2tciWm9yFvIAGk1wDUd0RjtGcUeUw=");
            //tt.dosyaSifreCozmeAll("jXz9sm2tciWm9yFvIAGk1wDUd0RjtGcUeUw=");
            






            // FileSystemRSA tt_rsa = new FileSystemRSA();
            // //tt_rsa.dosyaSifreleme("deneme.txt", "aa.txt");
            // tt_rsa.dosyaSifreCozme("aa.txt", "bb.txt");
            // TryRSA aab = new TryRSA();
            // aab.init();
            // String enc = aab.encrypt("Hello World!");
            // String denc = aab.decrypt(enc);
            // System.out.println(enc);
            // System.out.println(denc);
            // aab.printKeys();;
        }
        catch(Exception ignored){
            
        }
    }
}
