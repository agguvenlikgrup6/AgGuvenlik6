package org.uludag.bmb.Company;

import java.security.MessageDigest;
import java.util.Base64;

public class HashTry {
    // VERİLEN BİR BYTE DİZİSİNİN HASH'İNİ BULAN FONK. ALGORİTMA DEĞİŞEBİLİR MD-5 Bİ
    // TIK DAHA HIZLI
    /*
     * File a = new File("deneme.txt");
     * byte[] abc = Files.readAllBytes(a.toPath());
     * KOD PARÇASI BİR DOSYAYI BYTE'A ÇEVİRİYOR.
     */
    public static String getHash(byte[] inputBytes) {
        String hashValue = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(inputBytes);
            byte[] digestedBytes = messageDigest.digest();
        
            String abc = Base64.getEncoder().encodeToString(digestedBytes);
            hashValue = new String(abc).toLowerCase();
            System.out.println(hashValue);
        } catch (Exception e) {

        }
        return hashValue;
    }
}
