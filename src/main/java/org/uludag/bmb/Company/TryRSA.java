package org.uludag.bmb.Company;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class TryRSA {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    public void init() throws Exception{
        try{
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(4096); // minimumu 512
            KeyPair pair = generator.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
        }
        catch(Exception ignored){
    
        }
    }
    //Sadece public alır şifreleme ve çözme yapar.
    public void initJustPub(String pubk){
        try{
            X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(pubk));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpecPublic);
        }
        catch(Exception ignored) {
        }
    }
    //Sadece private alır şifreleme ve çözme yapar.
    public void initJustPri(String prik){
        try{
            PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(decode(prik));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpecPrivate);
        }
        catch(Exception ignored) {
        }
    }
    public void initFromString(String pubk, String prik){
        try{
            X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(pubk));
            PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(decode(prik));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpecPublic);
            privateKey = keyFactory.generatePrivate(keySpecPrivate);
        }
        catch(Exception ignored) {
        }
    }
    public String getPub(){ return encode(publicKey.getEncoded()); }
    public String getPri(){ return encode(privateKey.getEncoded()); }
    public String encrypt(String message) throws Exception{
        byte[] messageToBytes = message.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE,publicKey);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }
    private String encode(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }
    public String decrypt(String encryptedMessage) throws Exception{
        byte[] encryptedBytes = decode(encryptedMessage);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
        return new String(decryptedMessage,"UTF8");
    }
    private byte[] decode(String data){
        return Base64.getDecoder().decode(data);
    }
    public void printKeys(){
        System.out.println("Public --->>>" + encode(publicKey.getEncoded()));
        System.out.println("Private --->>>" + encode(privateKey.getEncoded()));
    }
}
