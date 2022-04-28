package org.uludag.bmb.controller.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.config.Config;
import org.uludag.bmb.beans.config.FileDataJson;
import org.uludag.bmb.beans.crypto.EncryptedFileData;

public class ConfigController {

    public static final void mapDbFile(FileDataJson fileData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            FileOutputStream fout;
            fout = new FileOutputStream(PropertiesReader.getProperty("mapDbFile"));
            JsonGenerator g = mapper.getFactory().createGenerator(fout);

            mapper.writeValue(g, fileData);
            fout.close();
            g.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public class Crypto {
        public static void Save(EncryptedFileData encryptedFileData) {
            File dir = new File(PropertiesReader.getProperty("dataFolder"));
            if (!dir.exists()) {
                dir.mkdir();
            }
        }
    }
}
