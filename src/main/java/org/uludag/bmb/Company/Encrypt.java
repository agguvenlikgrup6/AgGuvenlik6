package org.uludag.bmb.Company;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;

public class Encrypt {
    public void abc(){
        File f = new File("deneme.txt");
        try{
            FileReader fr = new FileReader(f);
            int c = fr.read();
            while(c != -1){
                char k = (char)c;
                System.out.println(k + " ");
                c = fr.read();
            }
            fr.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


}
