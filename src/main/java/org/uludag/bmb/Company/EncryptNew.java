package org.uludag.bmb.Company;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptNew {
    private SecretKey key;
    private int KEY_SIZE = 128;
    private int T_LEN = 128;
    private byte[] IVName;
    private byte[] IVFile;
    

    public void init() throws Exception{
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE);
        key = generator.generateKey();
    }
    // public String keyGenerate() throws Exception{
    //     KeyGenerator generator = KeyGenerator.getInstance("AES");
    //     generator.init(KEY_SIZE);
    //     key = generator.generateKey();
    //     String key_st = Base64.getEncoder().encodeToString(key.getEncoded());
    //     return key_st;
    // }
    private String encode(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }
    public void initFromStrings(String secretKey, String IV){
        key = new SecretKeySpec(decode(secretKey),"AES");
        this.IVName = decode(IV);
    }
    public void initFromStringsNew(String secretKey, String IV, String IV2){
        key = new SecretKeySpec(decode(secretKey),"AES");
        this.IVName = decode(IV);
        this.IVFile = decode(IV2);
    }
    private byte[] decode(String data){
        return Base64.getDecoder().decode(data);
    }
    public String encryptForExistFileName(String message) throws Exception{
        byte[] messageInBytes = message.getBytes();
        Cipher encryptionCipher = Cipher.getInstance("/AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IVName);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key,spec);
        byte[] encrptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encrptedBytes);
    }
    public String encryptForExistFile(String message) throws Exception{
        byte[] messageInBytes = message.getBytes();
        Cipher encryptionCipher = Cipher.getInstance("/AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IVFile);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key,spec);
        byte[] encrptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encrptedBytes);
    }
    public String encryptForNew(String message) throws Exception{
        byte[] messageInBytes = message.getBytes();
        Cipher encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        IVName = encryptionCipher.getIV();
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes);
    }
    public String decrypt(String encryptedMessage) throws Exception{
        byte[] messageInBytes = decode(encryptedMessage);
        Cipher decryptionCipher = Cipher.getInstance("/AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN,IVName);
        decryptionCipher.init(Cipher.DECRYPT_MODE,key,spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(messageInBytes);
        return new String(decryptedBytes);
    }
    public String decryptForName(String encryptedMessage) throws Exception{
        byte[] messageInBytes = decode(encryptedMessage);
        Cipher decryptionCipher = Cipher.getInstance("/AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN,IVName);
        decryptionCipher.init(Cipher.DECRYPT_MODE,key,spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(messageInBytes);
        return new String(decryptedBytes);
    }
    public String decryptForFile(String encryptedMessage) throws Exception{
        byte[] messageInBytes = decode(encryptedMessage);
        Cipher decryptionCipher = Cipher.getInstance("/AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN,IVFile);
        decryptionCipher.init(Cipher.DECRYPT_MODE,key,spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(messageInBytes);
        return new String(decryptedBytes);
    }
    public String s_keyCall(){ return encode(key.getEncoded());}
    public String ivCall(){ return encode(IVName);}
}
