package org.uludag.bmb.Company;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import org.uludag.bmb.Company.EncryptNew;
import org.uludag.bmb.Company.Encrypt;


public class FileSystem {
    private String textDosya;
    EncryptNew crypt = new EncryptNew();
    public void dosyaSifreleme(String old_file, String new_file){
        File f = new File(old_file);
        try{
            Scanner fr = new Scanner(f);
            while(fr.hasNextLine()){
                textDosya = fr.nextLine();
            }
            fr.close();
            File new_f = new File(new_file);
            if (new_f.createNewFile()){
                crypt.init();
                FileWriter myWriter = new FileWriter(new_f);
                myWriter.write(crypt.encryptForNew(textDosya));
                myWriter.close();
                System.out.println("Don't Forget These Keys !!!");
                System.out.println("Secret Key  --> " + crypt.s_keyCall());
                System.out.println("IV --> " + crypt.ivCall());
            }
            else{
                Scanner myObj = new Scanner(System.in);
                System.out.println("Secret Key -->>  ");
                String s_key = myObj.nextLine();
                Scanner myObj2 = new Scanner(System.in);
                System.out.println("IV -->>  ");
                String IV = myObj2.nextLine();
                FileWriter myWriter = new FileWriter(new_f);
                crypt.initFromStrings(s_key, IV);
                myWriter.write(crypt.encryptForExist(textDosya));
                myWriter.close();
            }
        }
        catch(Exception e){
        }
    }
    public void dosyaSifreCozme(String old_file, String new_file){
        File f = new File(old_file);
        try{
            Scanner fr = new Scanner(f);
            while(fr.hasNextLine()){
                textDosya =fr.nextLine();
            }
            fr.close();
            File new_f = new File(new_file);
            if (new_f.exists()){
                Scanner myObj = new Scanner(System.in);
                System.out.println("Secret Key -->>  ");
                String s_key = myObj.nextLine();
                Scanner myObj2 = new Scanner(System.in);
                System.out.println("IV -->>  ");
                String IV = myObj2.nextLine();
                FileWriter myWriter = new FileWriter(new_f);
                crypt.initFromStrings(s_key, IV);
                myWriter.write(crypt.decrypt(textDosya));
                myWriter.close();
            }
            else{
                new_f.createNewFile();
                Scanner myObj = new Scanner(System.in);
                System.out.println("Secret Key -->>  ");
                String s_key = myObj.nextLine();
                Scanner myObj2 = new Scanner(System.in);
                System.out.println("IV -->>  ");
                String IV = myObj2.nextLine();
                FileWriter myWriter = new FileWriter(new_f);
                crypt.initFromStrings(s_key, IV);
                myWriter.write(crypt.decrypt(textDosya));
                myWriter.close();
            }
        }
        catch(Exception e){
        }
    }
}
