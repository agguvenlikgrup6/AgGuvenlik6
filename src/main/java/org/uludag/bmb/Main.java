package org.uludag.bmb;

import java.net.URISyntaxException;
import java.io.IOException;

import org.uludag.bmb.Company.Encrypt;
import org.uludag.bmb.Company.MainFrame;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        //MainFrame myFrame = new MainFrame();
        // myFrame.initialize();
        Encrypt erc = new Encrypt();
        erc.abc();
    }
}
