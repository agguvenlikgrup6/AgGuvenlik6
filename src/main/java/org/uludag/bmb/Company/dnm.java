package org.uludag.bmb.Company;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class dnm {
    public void list(){
        ArrayList<String> abc = new ArrayList<String>();
        abc.add("/A");
        abc.add("/B");
        abc.add("/A/A1");
        abc.add("/A/A2");
        Collections.sort(abc);
        String abb;
        for(int i=1;i<abc.size();i++){
            abb = abc.get(0);
            if(abc.get(i).startsWith(abb)){
                abc.set(i,abc.get(i).replaceFirst(abc.get(0), "   "));
            }
        }
        for(int i = 0; i<abc.size();i++){
            System.out.println(abc.get(i));
        }
    }
}
