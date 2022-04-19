package org.uludag.bmb.cryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int AES_KEY_BIT = 256;

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static byte[] encrypt(byte[] pText, SecretKey secret, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secret, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] encryptedText = cipher.doFinal(pText);
        return encryptedText;
    }

    public static byte[] encryptWithPrefixIV(byte[] pText, SecretKey secret, byte[] iv) throws Exception {

        byte[] cipherText = encrypt(pText, secret, iv);

        byte[] cipherTextWithIv = ByteBuffer.allocate(iv.length + cipherText.length)
                .put(iv)
                .put(cipherText)
                .array();

        FileOutputStream fos = new FileOutputStream("unnamed-encry.txt");
        fos.write(cipherTextWithIv);
        fos.close();

        return cipherTextWithIv;

    }

    public static byte[] getFile(File f) {
        InputStream is = null;
        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            byte[] content = new byte[is.available()];
            is.read(content);
            return content;
        } catch (IOException e) {
            System.exit(1);
            e.printStackTrace();
            return null;
        }

    }

    public static String decrypt(byte[] cText, SecretKey secret, byte[] iv) throws Exception {

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, secret, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] plainText = cipher.doFinal(cText);

        FileOutputStream fos = new FileOutputStream("unnamed-decryp.txt");
        fos.write(plainText);
        fos.close();

        return new String(plainText, UTF_8);

    }

    public static String decryptWithPrefixIV(byte[] cText, SecretKey secret) throws Exception {

        ByteBuffer bb = ByteBuffer.wrap(cText);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        bb.get(iv);

        byte[] cipherText = new byte[bb.remaining()];
        bb.get(cipherText);

        String plainText = decrypt(cipherText, secret, iv);

        return plainText;

    }

    public static String keyToString(SecretKey secretKey) {
        byte encoded[] = secretKey.getEncoded();
        String encodedKey = Base64.getEncoder().encodeToString(encoded);
        return encodedKey;
    }

    public static SecretKey decodeKeyFromString(String keyStr) {
        byte[] decodedKey = Base64.getDecoder().decode(keyStr);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0,
                decodedKey.length, "AES");
        return secretKey;
    }

    public static void main(String[] args) throws Exception {
        SecretKey secretKey = CryptoUtils.getAESKey(AES_KEY_BIT);
        String keyString = keyToString(secretKey);
        SecretKey key = decodeKeyFromString(keyString);
        byte[] iv = CryptoUtils.getRandomNonce(IV_LENGTH_BYTE);
        byte[] encryptedText = Crypto.encryptWithPrefixIV(getFile(new File("a")), secretKey, iv);
        Crypto.decryptWithPrefixIV(encryptedText, key);
    }

}
