package org.uludag.bmb;

import java.net.URISyntaxException;
import java.io.IOException;

import org.uludag.bmb.Company.Encrypt;
import org.uludag.bmb.Company.EncryptNew;
import org.uludag.bmb.Company.MainFrame;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        // try{
        //     EncryptNew en = new EncryptNew();
        // }
        // catch(Exception e){

        //}
        try{
            Encrypt abc = new Encrypt();
            abc.initFromStrings("wQmDGWk65sinlKJXiBp8vw==","lrVjjTIGrOE5G4vf");
            String encryptedMessage = abc.encrypt("merhaba batuhan yazici.");
            String decryptedMessage = abc.decrypt(encryptedMessage);

            System.out.println("Encrypted Message  --> "+encryptedMessage);
            System.out.println("Decrypted Message  --> "+decryptedMessage);
            //abc.exportKeys();
        }
        catch(Exception ignored){
            
        }
    }
}
