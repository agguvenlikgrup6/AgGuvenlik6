package org.uludag.bmb.controller.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.entity.config.Config;

public class ConfigController {

    public static final void initializeLocalStorage(Config config) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            FileOutputStream fout;
            fout = new FileOutputStream(PropertiesReader.getProperty("configFile"));
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(fout, config);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String getLocalPath() {
        FileInputStream fin;
        ObjectMapper mapper = new ObjectMapper();
        try {
            fin = new FileInputStream(PropertiesReader.getProperty("configFile"));
            Config config = mapper.readValue(fin, Config.class);
            return config.getLocalDropboxPath();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
