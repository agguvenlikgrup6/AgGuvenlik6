package org.uludag.bmb.Company;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import org.uludag.bmb.Company.EncryptNew;
import org.uludag.bmb.Company.Encrypt;


public class FileSystem {
    private String textDosya;
    EncryptNew crypt = new EncryptNew();
    Encrypt aab = new Encrypt();
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
                aab.init();
                aab.exportKeys();
                FileWriter myWriter = new FileWriter(new_f);
                myWriter.write(aab.encrypt(textDosya));
                myWriter.close();
            }
            else{
                Scanner myObj = new Scanner(System.in);
                System.out.println("aaaa");
                String aaa = myObj.nextLine();
                System.out.println(aaa);
            }
            // FileWriter myWriter = new FileWriter(new_f);
            // myWriter.write(aab.encrypt(textDosya));
            // myWriter.close();
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
            if (new_f.createNewFile()){
                
            }
            FileWriter myWriter = new FileWriter(new_f);
            myWriter.write(aab.decrypt(textDosya));
            myWriter.close();
        }
        catch(Exception e){

        }
    }
}
