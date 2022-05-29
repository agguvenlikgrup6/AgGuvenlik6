package org.uludag.bmb.service.cryption;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.uludag.bmb.beans.crypto.EncryptedFileData;

public class Crypto {
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
                kpg.initialize(4096);
                return kpg.generateKeyPair();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }

        }

    }

    public static void main(String[] args) {
        String aa = KEY_EXCHANGE.encrypt("message", "MIIJQgIBADANBgkqhkiG9w0BAQEFAASCCSwwggkoAgEAAoICAQDNNetyoi-7tUFIL5hwmybyFlxLOe9dLMOAbteNXI8xt2CfD09Mbd84DDc1pOJ9A4e29V9-oqs9N7hVKGKGdW7SO5OSJnjqzSjEHQGNRwNq_R0uBE-KT0a-3Oukg0YY36wvc8FGLA3Drhg5XsBazDsYrv3KRbdV1aOb_Keo356U6L2tjaNGmIOkL0VU70FO92BUwEk4qwPJXTqYr5nr2BfabfzmBdQIeHNfGF9ch7mr2qNJ8bNhNU9mU3Y9ecyXfgZ87K1D7Yn3_DHkbXXEPjpUqBfgihTAFG7PJ5CHYxqH8rKlZOvGMav3_mcssONWv01DvceLbMBvmVbfKdFnHKNh-2WbeUdnpMEpxH8dgq9PfjVI342HEvyJsxXei0Cd5BcWMCPgWsBKCKZNQSqIyCzjBl7JMWoXimp0P11V-Lk_HNkiCECmDYd5nRqcweN6w9Xs2D37xsrnh1VuG4sGRMINdzZfDTXfAzbYVapPvCIvlgyo_rWZPuANX1ofQXJcfDI4pZc9exg5PWrz4Zeg-BxVvMI4R6k4CqdRjOW7w8EpQXjDw4APKgRC3IRCrKB6Ex7N1eBGN-j3yyThvq7cN5ZjnZ709My6hTfEe0FBdX33mo6iSysS0ZURXU83g9gyqYWl9P45zhmNHdmONKTbm02ZnZJt8R-hJfqQ6TE4tXysbwIDAQABAoICABFRSI8bxfPZ38PfIfn2wSDS53NnVNGqUmetpiRSBR-IvlWnlBiUeRVT92DGiy2waGAo16gD9qsSEoAIcKNKMh6TVBvHgixgDd2m8peUJsl9L_Y1lOsy6RR4TkZ-1-6HcBKjeriYq4l-hDrRlsgGAVxO5pbB4i8TLV05KbW9nhz3Tljg4aa2fjCso8KY9XKPIiKYBSKltyPjtmsy_nlOevM-LuzQELNHGIb6gCmt2beTM6gTOc3geTsSAtUJr0zlhlfXUICxmuAOxbyyMN46JiM4xqZDRzo1pZ6qIK9CmypePkDPTuC8SZGCQ_2CG_bl6l5NTxNvPqCF8mfSZQKGAl-Lgigz7GOIIc2ynyS2PtKQMlTbjpGXwprgtP1OM_THGlAsN_5rvU7KLtvpDjaqNhM0iG-k4MnlL2WIlGbExtDOtPIOMoSOBWdE-uHS-SZ4nZ7UQkmAbXspB5ke7Owq7GJ6z8axTUq-XD-UE3NhOn_I7DdCIN3pKr9UEcRaE8rstebkYYN821LqKn_KzsOR_hfvXvoX5x1K6lbCRNGaBtfGFHaqra20P6OR95QUFNEM2k--7rC5az-UtUWLjK9Iz0sVyFmRQpy78Rs0A3U--OiUces2fWTEY-XtPnPmAA3KDqAa9s2qzN_hiIqIWj7ifjfKKfswK-H-wuUxWE4ud6RpAoIBAQDcr3UHLgwGpKz9A6pGdEFnORaqoQv4tLHB_RzBFT6mSYXGjTMixPcBgX6RL1Uwl6tJOhPK9DmOce7u4jH-LJpUif7QT6YY1YUdBELd1UE6qaAWWYDlyk2EsnPL0i0dRsWZjgrE3dL_INDQx98tRRLlwLbBpruoVZ40sqPmsBAaXV77tNlyjIl55-8R3x16VlNT3ua25qyEAmk38AyTGjTl4kGgqDxQVB2TpYTt8zhD61T3i1Kg_J3lbdNkY4jCW-G3ZBRbwy6Zn8Sc6hJLy3HHVhqAAQRLzeuxdRW1By9O6h2hU00cck0BgYX4Dqsii9k1yT-a1I5vDpIg8DXgGlazAoIBAQDuDIcN27pC9s7DY3LDfw01nTX2EqI6f0RVawTx0Roq-AZwyISEI0-lCfS_0xLWb3br5suAhzxPnjndvHujybw3jOpJefyNR9TI3QmITgkEthZwgPG7UySN32UjYq8Al7hKRjGqNOJwDsF4boXbKTyPvzj6cabFtvfTiTMF4PWbJNhMhXW-mHRAdfVRiroFPvtMAiS1cogsXztFLTLlRlxhKyQu_dYJz8cnOvtOFOYeeby-OZChUh6rV_NPcWagQkLJb_GyM0oOAJgfWo98lSaREiCW84ImishD01b0Bl_SpAYCHzBsGpGDJfkt85GiBbxHAY_Zc5E8ZL56KWOdFBFVAoIBAQCV52UiJGgs1w1erG-sfemzWTcJXm3IWc1pTSqMCYdN4yFItr8mfg6e6jx0GGfQoyXJjvbE5NJ37PJ1OBbnZZrLlBUPoRrdQaSzCiL1lpdBZtmEdPW2oY24dpNrvu_ANEOjQ7YS9e8OOFc_ipURphrpD9fUgRJ1jPrzJulomO3HSudrKPSP9-CmeyKMzDa01BC0JvQnB1fmL9ETGy8feSy3ftI_dCfMFOcmVMeP7qeokyFbKTtOeWoz3z85sNbD1y_ip8ugbVupJLqkL-ePrrkxw04q5wXpDA5us260UhYTj7nizLfTe9zNChNbZXNf-wjnnTVkUcASth4S27hs6tc7AoIBAG4mI9P4yU41D-V_PdpSDtWxIgf0BVcWDpOLyqTuQbKCQ9CqKA9Qm4jde3_Ldlr9BsUPbTjS2shoiZQdpARc05fMv5gyNvdUtpQfG0sT-L5SVz3FnJU2WqK_LPhbsGiQy7iqlE6S2_hIC6PjWFLZ2OkW7ZGNR0MTb8dVI-PjfSJFwURfOOVxe9FcA6CwlXTZ-MPp9fxzu6j0g-N1j6hhj1ty7_JCiGjP2PJBK4msx_JwGDLZRiAvdHuKXc5zxa4AGxFEmE0ByJGEGt_fMqfOCCN2hNf0Y1PcQeCIiSKvVGWOei0_K1PKU2yhBJULsGaNStp8UYioHhsY7r5uKmbDo2UCggEAZ8k6LvgW6-5qO4Qo4P0D3eQC2qeYeXu600Drl-T65_o11P8C2s9zVTileW0xXjb_uuw16NQr_vF6Wn8Zm6W_WBZn7nTF8gfo5qf51hX5Of711YPsjF66yN5TVOhKhiBu9jNPZ7P9OHDTbg8i4VAb983cyTNtgkpibSi8b05XESXbYZm9mQa15Q8v73vvIYNnDGe0-vRc9FrcwLi5-mGdWC-Szg5MQUGLYu4WLt1LZd3lZA2LGusQ6X7N-EpBkbw641NGDg01_JzkPR3354XT1-Jnf7si2TQqQ6OIzz92njvjT1yCNX7HiZ8dIAklazZ4Kptq5vipi_QSA7-Wx2hsjQ==");
        System.out.println("");
    }

    public class KEY_EXCHANGE {
        public static String encrypt(String message, String key) {
            try {
                byte[] decoded = decode(key);
                RSAPublicKey rsaPublicKey = RSAPublicKey.getInstance(decoded);
                BigInteger modulus = rsaPublicKey.getModulus();
                BigInteger publicExponent = rsaPublicKey.getPublicExponent();
                RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, publicExponent);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                PublicKey generatedPublic = kf.generatePublic(keySpec);
                byte[] messageToBytes = message.getBytes();
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, generatedPublic);
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

        public static String decrypt(String encryptedMessage, String key) {
            PrivateKey privateKey = null;
            try {
                byte[] encryptedBytes = decode(encryptedMessage);
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
                return new String(decryptedMessage, "UTF8");
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
