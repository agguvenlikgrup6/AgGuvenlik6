package org.uludag.bmb;

import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Collection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.uludag.bmb.Company.FileSystemRSA;
import org.uludag.bmb.Company.Crypto;
import org.uludag.bmb.Company.HashTry;
import org.uludag.bmb.Company.TryRSA;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        try{
            // File a = new File("görselprog.pdf");
            // byte[] abc = Files.readAllBytes(a.toPath());
            // HashTry pp = new HashTry();
            // pp.getHash(abc);
            Collection<String> listOne = new ArrayList<>(Arrays.asList("milan","dingo","elpha","hafil","meat","iga","neeta.peeta"));
            Collection<String> listTwo = new ArrayList<>(Arrays.asList("hafil","iga","binga","mike","dingo"));
            Collection<String> similar = new HashSet<String>(listOne);
            Collection<String> different = new HashSet<String>();
            different.addAll(listOne);
            different.addAll(listTwo);
            similar.retainAll(listTwo);
            different.removeAll(similar);
            System.out.println("1-->  " + listOne);
            System.out.println("2-->  " + listTwo);
            System.out.println("similar-->  " + similar);
            System.out.println("different-->  " + different);


            //Crypto tt = new Crypto();
            //tt.dosyaSifrelemeAll("deneme.txt");
            //tt.dosyaAdSifreCozme("hwP5+EixvLljXHFsDJTERM3FeIaF74LTqAw=");
            //tt.dosyaSifreCozmeAll("O4h16EjeeP57lhG1pCM3T9xDQSWucj+pv+8=");







            //String newFileName = tt.fileNameEncrypt("deneme.txt");
            //tt.fileNameDecrypt("aK1Vv+PzjufbYkZQV5/fLtR6Dd1mARQTFRc=");
            //tt.fileEncrypt("deneme.txt", newFileName);
            //tt.dosyaSifreleme("deneme.txt","aa.txt");
            //tt.dosyaSifreCozmeAll("aa.txt");
            //tt.dosyaAdSifreCozme("fn86TfVxnSQgHZxL1g4F+Xf8IqCa630g3E0=");
            //tt.dosyaSifreCozmeAll("cOFfJh8TS1k+jMWAQNngIW8joPSplakUDUc=");
            






            // FileSystemRSA tt_rsa = new FileSystemRSA();
            // //tt_rsa.dosyaSifreleme("deneme.txt", "aa.txt");
            // tt_rsa.dosyaSifreCozme("aa.txt", "bb.txt");
            // TryRSA aab = new TryRSA();
            // aab.init();
            // String enc = aab.encrypt("Hello World!");
            // String denc = aab.decrypt(enc);
            // System.out.println(enc);
            // System.out.println(denc);
            // aab.printKeys();;
        }
        catch(Exception ignored){
            
        }
    }
}
