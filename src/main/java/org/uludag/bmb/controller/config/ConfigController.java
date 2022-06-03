package org.uludag.bmb.controller.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.beans.config.LocalConfig;
import org.uludag.bmb.beans.constants.Constants;
import org.uludag.bmb.beans.database.sharing.RecievedFile;
import org.uludag.bmb.beans.database.sharing.SharedFile;

public class ConfigController {
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

    public class SharedFileCredentials {
        public static final FileInputStream Save(SharedFile sharedFile) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                FileOutputStream fout;
                fout = new FileOutputStream(Constants.ACCOUNT.cacheSharedFileDirectory + sharedFile.getEncryptedName() + ".json");
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                mapper.writeValue(fout, sharedFile);
                fout.close();

                try {
                    return new FileInputStream(Constants.ACCOUNT.cacheSharedFileDirectory + sharedFile.getEncryptedName() + ".json");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static final RecievedFile Load(String encryptedName) {
            FileInputStream fin;
            ObjectMapper mapper = new ObjectMapper();
            try {
                fin = new FileInputStream(PropertiesReader.getProperty("configFile"));
                RecievedFile recievedFile = mapper.readValue(fin, RecievedFile.class);
                return recievedFile;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}
