package org.uludag.bmb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    /**
     *
     * resources/org/uludag/bmb/application.properties dosyası içerisinde bulunan
     * verilere doğrudan erişim
     *
     * @param propertyName değeri okunacak property
     */
    public static String getProperty(String propertyName) throws IOException {
        Properties properties;
        InputStream is = PropertiesReader.class.getResourceAsStream("/application.properties");
        properties = new Properties();
        properties.load(is);
        return properties.getProperty(propertyName);
    }
}