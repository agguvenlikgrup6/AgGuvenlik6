package org.uludag.bmb.oauth;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

public class Deneme {
    
    private final Logger LOGGER = Logger.getLogger(Deneme.class.getName());
    @Test
    public void main() throws SecurityException, IOException {
 
        LOGGER.info("Logger Name: "+LOGGER.getName());
         
        LOGGER.warning("Can cause ArrayIndexOutOfBoundsException");
        System.out.println(33);
         
    }
 
}