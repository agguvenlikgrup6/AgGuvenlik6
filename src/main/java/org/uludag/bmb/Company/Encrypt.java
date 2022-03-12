package org.uludag.bmb.Company;

import java.io.File;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Scanner;

public class Encrypt {
    public String textDosya = "";
    private SecretKey key;
    private int KEY_SIZE = 128;
    private Cipher encryptionCipher;
    private int T_LEN = 128;
    public void init() throws Exception{
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE);
        key = generator.generateKey();
    }

    public String encrypt(String message) throws Exception{
        byte[] messageInBytes = message.getBytes();
        encryptionCipher = Cipher.getInstance("/AES/GCM/NoPadding");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encrptedBytes);
    }

    public String decrypt(String encryptedMessage) throws Exception{
        byte[] messageInBytes = decode(encryptedMessage);
        Cipher decryptionCipher = Cipher.getInstance("/AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN,encryptionCipher.getIV());
        decryptionCipher.init(Cipher.DECRYPT_MODE,key,spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(messageInBytes);
        return new String(decryptedBytes);
    }
    private String encode(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }
    private byte[] decode(String data){
        return Base64.getDecoder().decode(data);
    }

    // public void abc(){
    //     File f = new File("deneme.txt");
    //     try{
    //         Scanner fr = new Scanner(f);
    //         while(fr.hasNextLine()){
    //             textDosya = fr.nextLine();
    //         }
    //         fr.close();
    //         KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
    //         SecretKey myDesKey = keygenerator.generateKey();

    //         Cipher desCipher;
    //         desCipher = Cipher.getInstance("DES");

    //         byte[] text = "No body can see me.".getBytes("UTF8");
    //         desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);


    //         byte[] textEncrypted = desCipher.doFinal(textDosya.getBytes(StandardCharsets.UTF_8));
    //         String s = new String(textEncrypted);
    //         System.out.println(s);

    //         desCipher.init(Cipher.DECRYPT_MODE, myDesKey);
    //         byte[] textDecrypted = desCipher.doFinal(textEncrypted);

    //         s = new String(textDecrypted);
    //         System.out.println(s);


    //         // FileReader fr = new FileReader(f);
    //         // int c = fr.read();
    //         // while(c != -1){
    //         //     char k = (char)c;
    //         //     System.out.println(k + " ");
    //         //     c = fr.read();
    //         // }
    //         // fr.close();
    //     }
    //     catch(Exception e){
    //         //e.printStackTrace();
    //         System.out.println("Exception");
    //     }
    // }


}
