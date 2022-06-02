package org.uludag.bmb.service.cryption;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.uludag.bmb.beans.crypto.EncryptedFileData;
import org.uludag.bmb.beans.database.sharing.RecievedFile;
import org.uludag.bmb.beans.database.sharing.SharedFile;
import org.uludag.bmb.controller.localconfig.LocalConfigController;
import org.uludag.bmb.operations.database.RecievedFileOperations;

public class Crypto {
    private static String RSA_MODE = "RSA/ECB/PKCS1Padding";
    private static final RecievedFileOperations recievedFileOperations = new RecievedFileOperations();
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int AES_KEY_BIT = 128;

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static EncryptedFileData encryptFile(File file) {
        byte[] fileData;
        try {
            fileData = getFileBytes(new FileInputStream(file.getAbsolutePath()));
            SecretKey secretKey = CryptoUtils.getAESKey(AES_KEY_BIT);
            byte[] iv = CryptoUtils.getRandomNonce(IV_LENGTH_BYTE);
            byte[] encryptedFileBytes = Crypto.encryptWithPrefixIV(fileData, secretKey, iv);
            byte[] encryptedFileNameBytes = Crypto.encryptWithPrefixIV(file.getName().getBytes(StandardCharsets.UTF_8),
                    secretKey, iv);

            InputStream encryptedFile = new ByteArrayInputStream(encryptedFileBytes);

            String encryptedName = Base64.getUrlEncoder().encodeToString(encryptedFileNameBytes);
            String key = Base64.getUrlEncoder().encodeToString(secretKey.getEncoded());

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

    private static String decryptNameWithIV(byte[] cText, SecretKey secret, byte[] iv) throws Exception {

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, secret, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] plainText = cipher.doFinal(cText);

        return new String(plainText, UTF_8);

    }

    private static byte[] decryptFileWithIV(byte[] cText, SecretKey secret, byte[] iv) throws Exception {

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, secret, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] plainText = cipher.doFinal(cText);

        return plainText;

    }

    public static String decryptName(byte[] cText, String secret) {
        SecretKey key = decodeKeyFromString(secret);

        ByteBuffer bb = ByteBuffer.wrap(cText);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        bb.get(iv);

        byte[] cipherText = new byte[bb.remaining()];
        bb.get(cipherText);

        String plainText;
        try {
            plainText = decryptNameWithIV(cipherText, key, iv);
            return plainText;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static byte[] decryptFile(byte[] cText, String secret) {
        SecretKey key = decodeKeyFromString(secret);
        
        ByteBuffer bb = ByteBuffer.wrap(cText);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        bb.get(iv);

        byte[] cipherText = new byte[bb.remaining()];
        bb.get(cipherText);

        try {
            return decryptFileWithIV(cipherText, key, iv);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SecretKey decodeKeyFromString(String keyStr) {
        byte[] decodedKey = Base64.getUrlDecoder().decode(keyStr.getBytes(StandardCharsets.UTF_8));
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0,
                decodedKey.length, "AES");
        return secretKey;
    }

    public class SHARE {
        public static KeyPair CREATE_KEY_PAIR() {
            try {
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                kpg.initialize(2048);
                return kpg.generateKeyPair();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }

        }

        public static void DECRYPT_PREVIEW(SharedFile sharedFile) {
            try {
                String myPrivateKey = LocalConfigController.Settings.LoadSettings().getPrivateRsaKey();
                String senderPublicKey = LocalConfigController.Settings.LoadSettings().getUserEmail();
                String decryptedKeyPart1 = KEY_EXCHANGE.decryptWithPrivate(sharedFile.getFileKeyPart1(), myPrivateKey);
                String decryptedKeyPart2 = KEY_EXCHANGE.decryptWithPrivate(sharedFile.getFileKeyPart2(), myPrivateKey);
                String decryptedKey = decryptedKeyPart1 + decryptedKeyPart2;
                String secondDecryptedKey = KEY_EXCHANGE.decryptWithPublic(decryptedKey, senderPublicKey);

                String decryptedFileName = Crypto.decryptName(
                        Base64.getUrlDecoder().decode(sharedFile.getEncryptedName().getBytes(StandardCharsets.UTF_8)),
                        secondDecryptedKey);

                recievedFileOperations.insert(
                        new RecievedFile(sharedFile.getEncryptedName(), decryptedFileName, secondDecryptedKey));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class KEY_EXCHANGE {
        private static PublicKey getPublicKey(String pubk) {
            try {
                X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(pubk));

                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePublic(keySpecPublic);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private static PrivateKey getPrivateKey(String prik) {
            try {
                PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(decode(prik));
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePrivate(keySpecPrivate);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static String encryptWithPublic(String message, String key) {
            try {
                byte[] messageToBytes = message.getBytes();
                Cipher cipher = Cipher.getInstance(RSA_MODE);
                cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(key));
                byte[] encryptedBytes = cipher.doFinal(messageToBytes);
                return encode(encryptedBytes);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static String encryptWithPrivate(String message, String key) {
            try {
                byte[] messageToBytes = message.getBytes();
                Cipher cipher = Cipher.getInstance(RSA_MODE);
                cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey(key));
                byte[] encryptedBytes = cipher.doFinal(messageToBytes);
                return encode(encryptedBytes);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private static String encode(byte[] data) {
            return Base64.getUrlEncoder().encodeToString(data);
        }

        public static String decryptWithPublic(String encryptedMessage, String key) {
            PublicKey publicKey = getPublicKey(key);
            try {
                byte[] encryptedBytes = decode(encryptedMessage);
                Cipher cipher = Cipher.getInstance(RSA_MODE);
                cipher.init(Cipher.DECRYPT_MODE, publicKey);
                byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
                return new String(decryptedMessage);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static String decryptWithPrivate(String encryptedMessage, String key) {
            PrivateKey privateKey = getPrivateKey(key);
            try {
                byte[] encryptedBytes = decode(encryptedMessage);
                Cipher cipher = Cipher.getInstance(RSA_MODE);
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
                return new String(decryptedMessage);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private static byte[] decode(String data) {
            return Base64.getUrlDecoder().decode(data);
        }
    }

}
