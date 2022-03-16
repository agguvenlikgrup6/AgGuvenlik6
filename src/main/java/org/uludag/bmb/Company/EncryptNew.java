package org.uludag.bmb.Company;

import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class EncryptNew {
    private SecretKey key;
    private int KEY_SIZE = 128;
    
    public String keyGenerate() throws Exception{
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE);
        key = generator.generateKey();
        String key_st = Base64.getEncoder().encodeToString(key.getEncoded());
        return key_st;
    }
}
