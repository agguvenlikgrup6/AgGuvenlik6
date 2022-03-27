package org.uludag.bmb.Company;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class FileSystemRSA {
    private String textDosya;
    TryRSA crypt = new TryRSA();
    public void dosyaSifreleme(String old_file, String new_file){
        File f = new File(old_file);
        try{
            Scanner fr = new Scanner(f);
            while(fr.hasNextLine()){
                textDosya = fr.nextLine();
            }
            fr.close();
            crypt.init();
            File new_f = new File(new_file);
            if (new_f.createNewFile()){
                FileWriter myWriter = new FileWriter(new_f);
                myWriter.write(crypt.encrypt(textDosya));
                myWriter.close();
                System.out.println("Don't Forget These Keys !!!");
                System.out.println("Public Key  --> " + crypt.getPub());
                System.out.println("Private Key --> " + crypt.getPri());
            }
            else{
                Scanner myObj = new Scanner(System.in);
                System.out.println("Public Key -->>  ");
                String pub_key = myObj.nextLine();
                Scanner myObj2 = new Scanner(System.in);
                System.out.println("Private Key -->>  ");
                String pri_key = myObj2.nextLine();
                FileWriter myWriter = new FileWriter(new_f);
                crypt.initFromString(pub_key, pri_key);
                myWriter.write(crypt.encrypt(textDosya));
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
                System.out.println("Public Key -->>  ");
                String pub_key = myObj.nextLine();
                Scanner myObj2 = new Scanner(System.in);
                System.out.println("Private Key -->>  ");
                String pri_key = myObj2.nextLine();
                FileWriter myWriter = new FileWriter(new_f);
                crypt.initFromString(pub_key, pri_key);
                myWriter.write(crypt.decrypt(textDosya));
                myWriter.close();
            }
            else{
                new_f.createNewFile();
                Scanner myObj = new Scanner(System.in);
                System.out.println("Public Key -->>  ");
                String pub_key = myObj.nextLine();
                Scanner myObj2 = new Scanner(System.in);
                System.out.println("Private Key -->>  ");
                String pri_key = myObj2.nextLine();
                FileWriter myWriter = new FileWriter(new_f);
                crypt.initFromString(pub_key, pri_key);
                myWriter.write(crypt.decrypt(textDosya));
                myWriter.close();
            }
        }
        catch(Exception e){
        }
    }
}
