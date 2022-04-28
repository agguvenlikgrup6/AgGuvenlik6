package org.uludag.bmb.controller.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.config.Config;
import org.uludag.bmb.beans.crypto.EncryptedFileData;

public class ConfigController {
    public class Settings {
        public static final void SaveSettings(Config config) {
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

        public static final Config LoadSettings() {
            FileInputStream fin;
            ObjectMapper mapper = new ObjectMapper();
            try {
                fin = new FileInputStream(PropertiesReader.getProperty("configFile"));
                Config config = mapper.readValue(fin, Config.class);
                return config;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

    public class Crypto {
        public static void Save(EncryptedFileData encryptedFileData) {
            File dir = new File(PropertiesReader.getProperty("dataFolder"));
            if (!dir.exists()) {
                dir.mkdir();
            }
            try {
                Map<String, Object> map = new HashMap<>();
                var pathDisplay = encryptedFileData.metadata.getPathDisplay();
                map.put("path", pathDisplay.substring(0, (pathDisplay.length() - encryptedFileData.metadata.getName().length())));
                map.put("name", encryptedFileData.name);
                map.put("key", encryptedFileData.key); 
                
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                mapper.writeValue(Paths.get(dir.getAbsolutePath() + "/" + encryptedFileData.metadata.getName()).toFile(), map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
