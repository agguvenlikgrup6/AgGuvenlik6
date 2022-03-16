package org.uludag.bmb;

import java.net.URISyntaxException;
import java.io.IOException;

import org.uludag.bmb.Company.Encrypt;
import org.uludag.bmb.Company.MainFrame;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        //MainFrame myFrame = new MainFrame();
        // myFrame.initialize();
        // Encrypt erc = new Encrypt();
        // erc.abc();
        try{
            Encrypt abc = new Encrypt();
            abc.initFromStrings("wQmDGWk65sinlKJXiBp8vw==","lrVjjTIGrOE5G4vf");
            String encryptedMessage = abc.encrypt("merhaba batuhan yazici.");
            String decryptedMessage = abc.decrypt(encryptedMessage);
            abc.dosyaSifreleme();
            abc.dosyaSifreCozme();
            System.out.println("Encrypted Message  --> "+encryptedMessage);
            System.out.println("Decrypted Message  --> "+decryptedMessage);
            //abc.exportKeys();
        }
        catch(Exception ignored){
            
        }
    }
}
