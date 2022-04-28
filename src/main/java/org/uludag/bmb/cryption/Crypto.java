package org.uludag.bmb.cryption;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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

import org.uludag.bmb.beans.crypto.EncryptedFileData;

public class Crypto {
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int AES_KEY_BIT = 256;

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static EncryptedFileData encryptFile(File file) {
        byte[] fileData;
        try {
            fileData = getFileBytes(new FileInputStream(file.getAbsolutePath()));
            SecretKey secretKey = CryptoUtils.getAESKey(AES_KEY_BIT);
            byte[] iv = CryptoUtils.getRandomNonce(IV_LENGTH_BYTE);
            byte[] encryptedFileBytes = Crypto.encryptWithPrefixIV(fileData, secretKey, iv);
            byte[] encryptedFileNameBytes = Crypto.encryptWithPrefixIV(file.getName().getBytes(), secretKey, iv);

            InputStream encryptedFile = new ByteArrayInputStream(encryptedFileBytes);
            String encryptedName = Base64.getEncoder().encodeToString(encryptedFileNameBytes);

            encryptedName = encryptedName.replaceAll("/", "*");
            String key = Base64.getEncoder().encodeToString(secretKey.getEncoded());

            return new EncryptedFileData(encryptedFile, encryptedName, key);

        } catch (Exception ex) {
            System.out.println("Sistem belirtilen dosyayı bulamıyor!");
            ex.printStackTrace();
            return null;
        }

    }

    private static byte[] encrypt(byte[] pText, SecretKey secret, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secret, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] encryptedText = cipher.doFinal(pText);
        return encryptedText;
    }

    private static byte[] encryptWithPrefixIV(byte[] pText, SecretKey secret, byte[] iv) throws Exception {

        byte[] cipherText = encrypt(pText, secret, iv);

        byte[] cipherTextWithIv = ByteBuffer.allocate(iv.length + cipherText.length)
                .put(iv)
                .put(cipherText)
                .array();

        return cipherTextWithIv;

    }

    private static byte[] getFileBytes(FileInputStream f) {
        try {
            byte[] content = new byte[f.available()];
            f.read(content);
            return content;
        } catch (IOException e) {
            System.exit(1);
            e.printStackTrace();
            return null;
        }

    }

    private static String decrypt(byte[] cText, SecretKey secret, byte[] iv) throws Exception {

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, secret, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] plainText = cipher.doFinal(cText);

        FileOutputStream fos = new FileOutputStream("unnamed-decryp.txt");
        fos.write(plainText);
        fos.close();

        return new String(plainText, UTF_8);

    }

    private static String decryptWithPrefixIV(byte[] cText, SecretKey secret) throws Exception {

        ByteBuffer bb = ByteBuffer.wrap(cText);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        bb.get(iv);

        byte[] cipherText = new byte[bb.remaining()];
        bb.get(cipherText);

        String plainText = decrypt(cipherText, secret, iv);

        return plainText;

    }

    private static SecretKey decodeKeyFromString(String keyStr) {
        byte[] decodedKey = Base64.getDecoder().decode(keyStr);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0,
                decodedKey.length, "AES");
        return secretKey;
    }

    // public static void main(String[] args) throws Exception {
    // SecretKey secretKey = CryptoUtils.getAESKey(AES_KEY_BIT);
    // String keyString = keyToString(secretKey);
    // SecretKey key = decodeKeyFromString(keyString);
    // byte[] iv = CryptoUtils.getRandomNonce(IV_LENGTH_BYTE);
    // byte[] encryptedText = Crypto.encryptWithPrefixIV(getFileBytes(new
    // File("a")), secretKey, iv);
    // Crypto.decryptWithPrefixIV(encryptedText, key);
    // }

}
