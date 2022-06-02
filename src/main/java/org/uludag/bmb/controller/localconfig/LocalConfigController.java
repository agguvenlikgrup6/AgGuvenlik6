package org.uludag.bmb.controller.localconfig;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.localconfig.LocalConfig;

public class LocalConfigController {
    public class Settings {
        public static final void SaveSettings(LocalConfig config) {
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

        public static final LocalConfig LoadSettings() {
            FileInputStream fin;
            ObjectMapper mapper = new ObjectMapper();
            try {
                fin = new FileInputStream(PropertiesReader.getProperty("configFile"));
                LocalConfig config = mapper.readValue(fin, LocalConfig.class);
                return config;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}
