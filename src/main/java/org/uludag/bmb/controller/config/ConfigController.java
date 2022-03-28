package org.uludag.bmb.controller.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.uludag.bmb.PropertiesReader;
import org.uludag.bmb.entity.config.Config;


public class ConfigController {

    public static final void initializeLocalStorage(Config config) {
        ObjectMapper mapper = new ObjectMapper();
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(PropertiesReader.getProperty("configFile"));
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(fout, config);
            fout.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
