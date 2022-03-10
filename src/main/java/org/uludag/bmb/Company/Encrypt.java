package org.uludag.bmb.Company;

import java.io.File;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Encrypt {
    public String textDosya = "";
    public void abc(){
        File f = new File("deneme.txt");
        try{
            Scanner fr = new Scanner(f);
            while(fr.hasNextLine()){
                textDosya = fr.nextLine();
            }
            fr.close();
            KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
            SecretKey myDesKey = keygenerator.generateKey();

            Cipher desCipher;
            desCipher = Cipher.getInstance("DES");

            byte[] text = "No body can see me.".getBytes("UTF8");
            desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);


            byte[] textEncrypted = desCipher.doFinal(textDosya.getBytes(StandardCharsets.UTF_8));
            String s = new String(textEncrypted);
            System.out.println(s);

            desCipher.init(Cipher.DECRYPT_MODE, myDesKey);
            byte[] textDecrypted = desCipher.doFinal(textEncrypted);

            s = new String(textDecrypted);
            System.out.println(s);


            // FileReader fr = new FileReader(f);
            // int c = fr.read();
            // while(c != -1){
            //     char k = (char)c;
            //     System.out.println(k + " ");
            //     c = fr.read();
            // }
            // fr.close();
        }
        catch(Exception e){
            //e.printStackTrace();
            System.out.println("Exception");
        }
    }


}
