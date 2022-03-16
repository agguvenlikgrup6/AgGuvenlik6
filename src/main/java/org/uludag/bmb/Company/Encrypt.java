package org.uludag.bmb.Company;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Scanner;

public class Encrypt {
    public void dosyaSifreleme(){
        File f = new File("deneme.txt");
        try{
            Scanner fr = new Scanner(f);
            while(fr.hasNextLine()){
                textDosya = fr.nextLine();
            }
            fr.close();
            File new_f = new File("tt.txt");
            new_f.createNewFile();
            FileWriter myWriter = new FileWriter(new_f);
            myWriter.write(encrypt(textDosya));
            myWriter.close();
        }
        catch(Exception e){

        }
    }

    public void dosyaSifreCozme(){
        File f = new File("tt.txt");
        try{
            Scanner fr = new Scanner(f);
            while(fr.hasNextLine()){
                textDosya =fr.nextLine();
            }
            fr.close();
            File new_f = new File("tt3.txt");
            new_f.createNewFile();
            FileWriter myWriter = new FileWriter(new_f);
            myWriter.write(decrypt(textDosya));
            myWriter.close();
        }
        catch(Exception e){

        }
    }
    public String textDosya = "";
    private SecretKey key;
    private int KEY_SIZE = 128;
    private int T_LEN = 128;
    private byte[] IV;
    public void init() throws Exception{
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE);
        key = generator.generateKey();
    }

    public void initFromStrings(String secretKey, String IV){
        key = new SecretKeySpec(decode(secretKey),"AES");
        this.IV = decode(IV);
    }

    public String encryptOld(String message) throws Exception{
        byte[] messageInBytes = message.getBytes();
        Cipher encryptionCipher = Cipher.getInstance("/AES/GCM/NoPadding");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        IV = encryptionCipher.getIV();
        byte[] encrptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encrptedBytes);
    }


    public String encrypt(String message) throws Exception{
        byte[] messageInBytes = message.getBytes();
        Cipher encryptionCipher = Cipher.getInstance("/AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key,spec);
        byte[] encrptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encrptedBytes);
    }

    public String decrypt(String encryptedMessage) throws Exception{
        byte[] messageInBytes = decode(encryptedMessage);
        Cipher decryptionCipher = Cipher.getInstance("/AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN,IV);
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
    public void exportKeys(){
        System.out.println("SecretKey  --> "+encode(key.getEncoded()));
        System.out.println("IV  --> "+encode(IV));
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
