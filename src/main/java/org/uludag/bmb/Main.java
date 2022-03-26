package org.uludag.bmb;

import java.net.URISyntaxException;
import java.io.File;
import java.io.IOException;

import org.uludag.bmb.Company.FileSystem;
import org.uludag.bmb.Company.TryRSA;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        try{
            //FileSystem tt = new FileSystem();
            //tt.dosyaSifreleme("deneme.txt", "aa.txt");
            //tt.dosyaSifreCozme("aa.txt","dd.txt");
            TryRSA rsa = new TryRSA();
            rsa.init();
            String encryptedMessage = rsa.encrypt("Hello World!");
            String decryptedMessage = rsa.decrypt(encryptedMessage);

            System.out.println(encryptedMessage);
            System.out.println(decryptedMessage);
        }
        catch(Exception ignored){
            
        }
    }
}
