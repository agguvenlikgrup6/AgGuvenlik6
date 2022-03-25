package org.uludag.bmb.controller.gui;

import java.io.IOException;

import org.uludag.bmb.entity.dropbox.DbAccount;
import org.uludag.bmb.entity.dropbox.DbClient;

import javafx.scene.Scene;

public class Controller {
    public DbClient client;
    public DbAccount account;

    public Controller() {
        this.account = new DbAccount();
        this.client = new DbClient(account);
    }
    
}
