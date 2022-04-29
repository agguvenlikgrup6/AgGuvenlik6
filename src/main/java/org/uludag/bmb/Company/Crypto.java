package org.uludag.bmb.Company;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import org.uludag.bmb.Company.EncryptNew;
import org.uludag.bmb.Company.Encrypt;


public class Crypto {
    private String textDosya;
    EncryptNew crypt = new EncryptNew();
    public synchronized void dosyaSifrelemeAll(String old_file){
        File f = new File(old_file);
        try{
            Scanner fr = new Scanner(f);
            crypt.init();
            String new_file = dosyaAdSifreleme(old_file);
            File new_f = new File(new_file);
            new_f.createNewFile();
            FileWriter myWriter = new FileWriter(new_f);
            while(fr.hasNextLine()){
                textDosya = fr.nextLine();
                myWriter.write(crypt.encryptForNew(textDosya));
            }
            fr.close();
            myWriter.close();
            System.out.println("File IV --> " + crypt.ivCall());
        }
        catch(Exception e){
        }
    }
    public void dosyaSifreCozmeAll(String old_file){
        File f = new File(old_file);
        try{
            Scanner fr = new Scanner(f);
            Scanner myObj = new Scanner(System.in);
            System.out.println("Secret Key -->>  ");
            String s_key = myObj.nextLine();
            Scanner myObj2 = new Scanner(System.in);
            System.out.println("Name IV -->>  ");
            String IV = myObj2.nextLine();
            Scanner myObj3 = new Scanner(System.in);
            System.out.println("File IV -->>  ");
            String IV2 = myObj2.nextLine();
            crypt.initFromStringsNew(s_key, IV, IV2);
            String decryptedFileName = crypt.decryptForName(old_file);
            File new_f = new File(decryptedFileName);
            if (new_f.exists()){
                FileWriter myWriter = new FileWriter(new_f);
                while(fr.hasNextLine()){
                    textDosya =fr.nextLine();
                    myWriter.write(crypt.decryptForFile(textDosya));
                }
                fr.close();
                myWriter.close();
            }
            else{
                new_f.createNewFile();
                FileWriter myWriter = new FileWriter(new_f);
                while(fr.hasNextLine()){
                    textDosya =fr.nextLine();
                    myWriter.write(crypt.decryptForFile(textDosya));
                }
                fr.close();
                myWriter.close();
            }
        }
        catch(Exception e){
        }
    }
    public String dosyaAdSifreleme(String file_name){
        try{
            crypt.init();
            String encryptedFileName = crypt.encryptForNew(file_name);
            System.out.println("Don't Forget These Keys !!!");
            System.out.println("Secret Key --> " + crypt.s_keyCall());
            System.out.println("Name IV --> " + crypt.ivCall());
            return encryptedFileName;
        }
        catch(Exception e){
            String a = "aa";
            return a;
        }

    }
    public String dosyaAdSifreCozme(String file_name){
        try{
            Scanner myObj = new Scanner(System.in);
            System.out.println("Secret Key -->>  ");
            String s_key = myObj.nextLine();
            Scanner myObj2 = new Scanner(System.in);
            System.out.println("Name IV -->>  ");
            String IV = myObj2.nextLine();
            crypt.initFromStrings(s_key, IV);
            String decryptedFileName = crypt.decryptForName(file_name);
            System.out.println(decryptedFileName);
            return decryptedFileName;
        }
        catch(Exception e){
            String a = "aa";
            return a;
        }
    }
}
