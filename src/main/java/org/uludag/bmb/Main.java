package org.uludag.bmb;

import java.io.IOException;
import java.net.URISyntaxException;

import org.uludag.bmb.Company.MainFrame;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        MainFrame myFrame = new MainFrame();
        myFrame.initialize();
    }
}
