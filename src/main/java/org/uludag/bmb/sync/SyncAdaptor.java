package org.uludag.bmb.sync;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

public class SyncAdaptor extends FileAlterationListenerAdaptor {

    @Override
    public void onFileChange(File file) {
        System.out.println("Dosya değiştirildi!");
        try {
            System.out.println(file.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileCreate(File file) {
        System.out.println("Dosya oluşturuldu!");
        
        
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("Dosya silindi!");
        try {
            System.out.println(file.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
